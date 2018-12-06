package org.usi.ir.lp.parsers;

import java.util.List;

public interface HTMLParser {
    public enum Type {
        MOVIE,
        TV_SHOW,
        UNKNOWN
    }

    String extractTitle(String document);
    String extractimageUrl(String document);
    Integer extractDuration(String document);
    List<String> extractGenres(String document);
    String extractReleaseDate(String document);
    String extractDescription(String document);
    String extractRating(String document);

    Type getType(String document);
    String extractLink(String document);
}
