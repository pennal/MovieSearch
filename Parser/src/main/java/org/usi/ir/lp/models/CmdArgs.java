package org.usi.ir.lp.models;

import picocli.CommandLine;

public class CmdArgs {
    @CommandLine.Option(names = {"--imdb" }, description = "Path to the IMDB Dump")
    private String imdbPath;

    @CommandLine.Option(names = {"--allmovie" }, description = "Path to the AllMovie Dump")
    private String allMoviePath;

    @CommandLine.Option(names = {"--out" }, description = "Path to the output JSON File")
    private String jsonPath;

    public String getImdbPath() {
        return imdbPath;
    }

    public void setImdbPath(String imdbPath) {
        this.imdbPath = imdbPath;
    }

    public String getAllMoviePath() {
        return allMoviePath;
    }

    public void setAllMoviePath(String allMoviePath) {
        this.allMoviePath = allMoviePath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
}
