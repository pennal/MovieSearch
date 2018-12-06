package org.usi.ir.lp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.usi.ir.lp.models.AllMoviesData;
import org.usi.ir.lp.models.CmdArgs;
import org.usi.ir.lp.models.IMDBData;
import org.usi.ir.lp.models.Movie;
import org.usi.ir.lp.parsers.AllMovieParser;
import org.usi.ir.lp.parsers.HTMLParser;
import org.usi.ir.lp.parsers.IMDBParser;
import org.usi.ir.lp.utils.TextUtils;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {


        CmdArgs app = CommandLine.populateCommand(new CmdArgs(), args);

        List<Movie> imdbMovies = new ArrayList<>();
        List<Movie> allMovieMovies = new ArrayList<>();


        if (app.getJsonPath() == null || app.getJsonPath().isEmpty()) {
            System.err.println("Missing out path for the JSON file!");
            return;
        }

        if (app.getImdbPath() != null && !app.getImdbPath().isEmpty()) {

            List<File> htmlFiles = listHtmlFilesAtPath(app.getImdbPath());
            // Iterate over all of the movies, and create a Movie instance for all of them
            System.err.println("Parsing imdb data");
            imdbMovies = parseImdb(htmlFiles);
        }

        if (app.getAllMoviePath() != null && !app.getAllMoviePath().isEmpty()) {
            List<File> htmlFiles = listHtmlFilesAtPath(app.getAllMoviePath());
            // Iterate over all of the movies, and create a Movie instance for all of them
            System.err.println("Parsing AllMovie data");
            allMovieMovies = parseAllMovie(htmlFiles);
        }

        List<Movie> mergedMovieLists = mergeMovieLists(imdbMovies, allMovieMovies);


        Gson gson = new Gson();
        String json = gson.toJson(mergedMovieLists, new TypeToken<List<Movie>>() {}.getType());

        try {
            Files.write(Paths.get(app.getJsonPath()), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Movie> mergeMovieLists(List<Movie>... lists) {
        if (lists.length == 1) {
            // Only one list is provided, return it
            return lists[0];
        }

        // Map title -> movie
        Map<String, Movie> movieMap = new ConcurrentHashMap<>();

        int mergeCount = 0;

        for (List<Movie> currentList: lists) {
            // Iterate over each element
            for (Movie currentMovie: currentList) {

                List<String> cleanWords = TextUtils.removeStopWords(currentMovie.getTitle());
                String movieKey = String.join(" ", cleanWords);

                // This is a bit of a hack, but in some cases the movie key may be empty and merges everything at random
                if (movieKey.isEmpty()) {
                    movieKey = currentMovie.getTitle();
                }

                // Check if the element is in the map
                Movie existingMovie = movieMap.get(movieKey);

                if (existingMovie != null) {
                    System.err.println("Movie already exists: " + existingMovie.getTitle() + "...Merging [" + movieKey + "]");
                    mergeCount++;
                    // handle merge
                    // FIXME: This is awful, bot for now it will do
                    if (existingMovie.getImdbData() == null && currentMovie.getImdbData() != null) { // This was taken from allmovie, simply add the imdb data
                        existingMovie.setImdbData(currentMovie.getImdbData());
                    } else if (existingMovie.getAllMoviesData() == null && currentMovie.getAllMoviesData() != null) { // This was taken from imdb, simply add the allmovie data
                        existingMovie.setAllMoviesData(currentMovie.getAllMoviesData());
                    } else {
                        System.err.println("Could not find the source!");
                    }
                } else {
                    movieMap.put(movieKey, currentMovie);
                }
            }
        }

        System.err.println("MergeCount: " + mergeCount);

        return new ArrayList<>(movieMap.values());
    }

    public static List<File> listHtmlFilesAtPath(String path) throws FileNotFoundException {
        File directory = new File(path);

        if (!directory.exists()) {
            System.err.println(directory.getAbsolutePath() + " does not exist!");
            throw new FileNotFoundException("Directory does not exist!");
        }

        Collection<File> allFiles = FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);


        return allFiles.stream().filter(file -> FilenameUtils.getExtension(file.getAbsolutePath()).equals("html")).collect(Collectors.toList());
    }

    public static List<Movie> parseImdb(List<File> htmlFiles) {
        Map<String, Movie> imdbFilteredMovies = new ConcurrentHashMap<>();
        htmlFiles.parallelStream().map(file -> {
            HTMLParser htmlParser = new IMDBParser();
            try {
                String fileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

                System.out.println("Using file at path " + file.getAbsolutePath());

                HTMLParser.Type type = htmlParser.getType(fileContents);

                if (type.equals(HTMLParser.Type.MOVIE)) {
                    String title = htmlParser.extractTitle(fileContents);
                    String imageUrl = htmlParser.extractimageUrl(fileContents);
                    Integer duration = htmlParser.extractDuration(fileContents);
                    List<String> genres = htmlParser.extractGenres(fileContents);
                    String releaseDate = htmlParser.extractReleaseDate(fileContents);
                    String description = htmlParser.extractDescription(fileContents);


                    Movie m = new Movie();
                    m.setTitle(title);
                    m.setImageUrl(imageUrl);
                    m.setDuration(duration);
                    m.setGenres(genres);
                    m.setReleaseDate(releaseDate);
                    m.setDescription(description);


                    String link = htmlParser.extractLink(fileContents);
                    String rating = htmlParser.extractRating(fileContents);

                    IMDBData imdbData = new IMDBData();
                    imdbData.setLink(link);
                    imdbData.setRating(rating);

                    m.setImdbData(imdbData);
                    return m;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).forEach(movie -> {
            String movieTitle = movie.getTitle();

            if (!imdbFilteredMovies.containsKey(movieTitle)) {
                imdbFilteredMovies.put(movieTitle, movie);
            } else {
                System.err.println("Found a duplicate: " + movieTitle);
            }
        });


        return new ArrayList<>(imdbFilteredMovies.values());
    }

    public static List<Movie> parseAllMovie(List<File> htmlFiles) {
        List<Movie> firstPass = htmlFiles.parallelStream().map(file -> {
            HTMLParser htmlParser = new AllMovieParser();
            try {
                String fileContents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));

                System.out.println("Using file at path " + file.getAbsolutePath());

                HTMLParser.Type type = htmlParser.getType(fileContents);

                if (type.equals(HTMLParser.Type.MOVIE)) {
                    String title = htmlParser.extractTitle(fileContents);
                    String imageUrl = htmlParser.extractimageUrl(fileContents);
                    Integer duration = htmlParser.extractDuration(fileContents);
                    List<String> genres = htmlParser.extractGenres(fileContents);
                    String releaseDate = htmlParser.extractReleaseDate(fileContents);
                    String description = htmlParser.extractDescription(fileContents);


                    Movie m = new Movie();
                    m.setTitle(title);
                    m.setImageUrl(imageUrl);
                    m.setDuration(duration);
                    m.setGenres(genres);
                    m.setReleaseDate(releaseDate);
                    m.setDescription(description);


                    String link = htmlParser.extractLink(fileContents);
                    String rating = htmlParser.extractRating(fileContents);

                    AllMoviesData allMoviesData = new AllMoviesData();
                    allMoviesData.setLink(link);
                    allMoviesData.setRating(rating);

                    m.setAllMoviesData(allMoviesData);
                    return m;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());


        // We now need to group together all the movies which contain a common URL
        // First, change everyting to a map so we have URL -> Movie
        Map<String, Set<Movie>> urlToMovieMap = new HashMap<>();

        // Group them by common URL
        firstPass.stream().forEach(movie -> {
            // Add it to the set of movies
            if (movie.getTitle() != null) {
                Set<Movie> movies = urlToMovieMap.get(movie.getTitle());
                if (movies == null) {
                    movies = new HashSet<>();
                    urlToMovieMap.put(movie.getTitle(), movies);
                }
                movies.add(movie);
            } else {
                System.out.println(movie.getAllMoviesData().getLink());
            }
        });

        // Now iterate over all keys, and merge the objects when there are multiple fields
        List<Movie> finalList = new ArrayList<>();
        urlToMovieMap.entrySet().forEach(stringSetEntry -> {
            String title = stringSetEntry.getKey();
            Set<Movie> movies = stringSetEntry.getValue();

            Movie m = new Movie();

            for (Movie current: movies) {
                if (current.getTitle() != null && m.getTitle() == null) {
                    m.setTitle(current.getTitle());
                }

                if (current.getReleaseDate() != null && m.getReleaseDate() == null) {
                    m.setReleaseDate(current.getReleaseDate());
                }

                if (current.getGenres() != null && m.getGenres() == null) {
                    m.setGenres(current.getGenres());
                }

                if (current.getDescription() != null && m.getDescription() == null) {
                    m.setDescription(current.getDescription());
                }

                if (current.getDuration() != null && m.getDuration() == null) {
                    m.setDuration(current.getDuration());
                }

                if (current.getImageUrl() != null && m.getImageUrl() == null) {
                    m.setImageUrl(current.getImageUrl());
                }

                if (m.getAllMoviesData() == null) {
                    m.setAllMoviesData(new AllMoviesData());
                }

                if (current.getAllMoviesData() != null) {
                    if (current.getAllMoviesData().getLink() != null && m.getAllMoviesData().getLink() == null) {


                        boolean isValid = List.of("/review", "/cast-crew", "/releases", "/related", "/showtimes", "/awards").stream().noneMatch(urlPath -> {
                            return current.getAllMoviesData().getLink().contains(urlPath);
                        });
                        if (isValid) {
                            m.getAllMoviesData().setLink(current.getAllMoviesData().getLink());
                        }
                    }

                    if (current.getAllMoviesData().getRating() != null && m.getAllMoviesData().getRating() == null) {
                        m.getAllMoviesData().setRating(current.getAllMoviesData().getRating());
                    }
                }
            }


            finalList.add(m);
        });

        return finalList;
    }
}
