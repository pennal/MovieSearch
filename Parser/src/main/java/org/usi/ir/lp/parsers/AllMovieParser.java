package org.usi.ir.lp.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.usi.ir.lp.utils.RegexUtils;

import java.util.List;
import java.util.stream.Collectors;

public class AllMovieParser implements HTMLParser {
    @Override
    public String extractTitle(String document) {
        Document doc = Jsoup.parse(document);

        Elements titleElements = doc.select(".movie-title");
        if (titleElements.isEmpty()) {
            return null;
        }

        String rawTitle = titleElements.get(0).text();

        // Clean up the title by removing the year
        String yearExtractPattern = ".*\\w*(\\(.*\\)$)";
        List<String> res = RegexUtils.executeRegex(yearExtractPattern, rawTitle, true);

        if (res.size() >= 2) {
            String yearIndicator = res.get(1);
            rawTitle = rawTitle.replace(yearIndicator, "");
        }


        return rawTitle.trim();
    }

    @Override
    public String extractimageUrl(String document) {
        Document doc = Jsoup.parse(document);

        Elements imageElements = doc.select("meta[name=\"image\"]");

        if (imageElements.isEmpty()) {
            return null;
        }

        return imageElements.get(0).attr("content");
    }

    @Override
    public Integer extractDuration(String document) {
        Document doc = Jsoup.parse(document);

        Elements durationElements = doc.select("hgroup[class=\"details\"]");

        if (durationElements.isEmpty()) {
            return null;
        }

        Elements children = durationElements.get(0).children();

        // Iterate over all the children, until you find one that contains the Run Time word
        for (int i = 0; i < children.size(); i++) {
            String duration = children.get(i).text();

            if (duration.contains("Run Time")) {
                // Parse the duration to convert it to int
                String durationPattern = "(\\d*)h?\\s*(\\d*)min?";
                List<String> matches = RegexUtils.executeRegex(durationPattern, duration, true).stream().filter(match -> !match.isEmpty()).collect(Collectors.toList());

                if (matches.isEmpty()) {
                    return null;
                }

                // Check how many matches there are
                if (matches.size() >= 3) {
                    // We have hours and minutes
                    return Integer.parseInt(matches.get(1)) * 60 + Integer.parseInt(matches.get(2));
                } else if (matches.size() >= 2) {
                    // We only have minutes
                    return Integer.parseInt(matches.get(1));
                }
                return null;
            }
        }

        return null;
    }

    @Override
    public List<String> extractGenres(String document) {
        Document doc = Jsoup.parse(document);

        Elements genresElements = doc.select("span[class=\"header-movie-genres\"] > a");

        if (genresElements.isEmpty()) {
            return null;
        }

        return genresElements.stream().map(genreTag -> genreTag.text()).collect(Collectors.toList());

    }

    @Override
    public String extractReleaseDate(String document) {
        Document doc = Jsoup.parse(document);

        Elements releaseDateElements = doc.select("hgroup[class=\"details\"]").get(0).children();

        for (int i = 0; i < releaseDateElements.size(); i++) {
            if (releaseDateElements.get(i).text().contains("Release Date")) {
                Elements releaseDateChildren = releaseDateElements.get(i).children();

                return releaseDateChildren.get(0).select("span").text();
            }
        }
        return null;
    }

    @Override
    public String extractDescription(String document) {
        Document doc = Jsoup.parse(document);

        // $('').text().trim()
        Elements descriptionElements = doc.select("div[itemprop=\"description\"]");

        if (descriptionElements.isEmpty()) {
            return null;
        }

        return descriptionElements.get(0).text().trim();
    }

    @Override
    public String extractRating(String document) {
        Document doc = Jsoup.parse(document);

        Elements ratingElements = doc.select(".allmovie-rating.rating-allmovie-5");

        if (ratingElements.isEmpty()) {
            return null;
        }

        return ratingElements.get(0).text().trim();
    }

    @Override
    public Type getType(String document) {

        String title = this.extractTitle(document);

        if (title == null) {
            return Type.UNKNOWN;
        }

        return title.contains("TV Series") ? Type.TV_SHOW : Type.MOVIE;
    }

    @Override
    public String extractLink(String document) {
        Document doc = Jsoup.parse(document);

        Elements urlElements = doc.select("link[rel=\"canonical\"]");

        if (urlElements.isEmpty()) {
            return null;
        }

        return urlElements.get(0).attr("href");
    }
}
