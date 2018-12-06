package org.usi.ir.lp.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.usi.ir.lp.utils.RegexUtils;

import java.util.List;
import java.util.stream.Collectors;


public class IMDBParser implements HTMLParser {
    @Override
    public String extractTitle(String document) {
        Document doc = Jsoup.parse(document);

        Elements titleElements = doc.select(".title_wrapper > h1");
        if (titleElements.size() == 0) {
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

        Elements imageElements = doc.select("meta[property=\"og:image\"]");

        if (imageElements.isEmpty()) {
            return null;
        }

        return imageElements.get(0).attr("content");
    }

    @Override
    public Integer extractDuration(String document) {
        Document doc = Jsoup.parse(document);

        Elements durationElements = doc.select("time");

        if (durationElements.isEmpty()) {
            return null;
        }

        String duration = durationElements.get(0).text();

        // Parse the duration to convert it to int
        String durationPattern = "(\\d*)h?\\s*(\\d*)min?";
        List<String> matches = RegexUtils.executeRegex(durationPattern, duration, true);

        if (matches.isEmpty()) {
            return null;
        }

        // Check how many matches there are
        if (matches.size() >= 3 && !matches.get(2).isEmpty()) {
            // We have hours and minutes

            return Integer.parseInt(matches.get(1)) * 60 + Integer.parseInt(matches.get(2));

        } else if (matches.size() >= 2) {
            // We only have minutes
            try {
                return Integer.parseInt(matches.get(2));
            } catch (NumberFormatException e) {
                // This is a last shot
                return Integer.parseInt(matches.get(1));
            }
        }

        return null;
    }

    @Override
    public List<String> extractGenres(String document) {
        // $('')
        Document doc = Jsoup.parse(document);

        Elements genreElements = doc.select(".subtext > a[href^=\"/search/title?genre\"]");
        if (genreElements.isEmpty()) {
            return null;
        }

        // Get each of the elements, and extract the genre
        return genreElements.stream().map(Element::text).collect(Collectors.toList());
    }

    @Override
    public String extractReleaseDate(String document) {
        Document doc = Jsoup.parse(document);

        Elements yearElements = doc.select(".subtext > a[href*=\"/releaseinfo\"]");
        if (yearElements.isEmpty()) {
            return null;
        }

        return yearElements.get(0).text();
    }

    @Override
    public String extractDescription(String document) {
        Document doc = Jsoup.parse(document);

        Elements descriptionElements = doc.select(".summary_text");

        if (descriptionElements.isEmpty()) {
            return null;
        }

        return descriptionElements.get(0).text();
    }

    @Override
    public Type getType(String document) {
        //$('').attr('content')
        Document doc = Jsoup.parse(document);

        Elements typeElements = doc.select("meta[property=\"og:type\"]");

        if (typeElements.isEmpty()) {
            return Type.UNKNOWN;
        }

        String contentType = typeElements.get(0).attr("content");

        if (contentType.contains("movie")) {
            return Type.MOVIE;
        } else if (contentType.contains("tv_show")) {
            return Type.TV_SHOW;
        } else {
            return Type.UNKNOWN;
        }

    }

    @Override
    public String extractLink(String document) {
        Document doc = Jsoup.parse(document);

        Elements urlElements = doc.select("meta[property=\"og:url\"]");

        if (urlElements.isEmpty()) {
            return null;
        }

        return urlElements.get(0).attr("content");
    }

    @Override
    public String extractRating(String document) {
        Document doc = Jsoup.parse(document);

        Elements ratingElements = doc.select("span[itemprop=\"ratingValue\"]");

        if (ratingElements.isEmpty()) {
            return null;
        }

        return ratingElements.get(0).text();
    }
}
