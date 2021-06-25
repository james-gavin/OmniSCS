package com.jmsgvn.staff;

import com.jmsgvn.util.DataSource;

import java.util.ArrayList;
import java.util.List;

public class Report {

    private static List<Report> reports = new ArrayList<>();

    private int id;
    private String playerName;
    private long reportTimestamp;
    private String server;
    private String playerReported;
    private String message;
    private boolean sovled;
    private String solver;
    private long solvedTimestamp;

    public Report(int id, String playerName, long reportTimestamp, String server, String playerReported, String message, boolean sovled, String solver, long solvedTimestamp) {
        this.id = id;
        this.playerName = playerName;
        this.reportTimestamp = reportTimestamp;
        this.server = server;
        this.playerReported = playerReported;
        this.message = message;
        this.sovled = sovled;
        this.solver = solver;
        this.solvedTimestamp = solvedTimestamp;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getReportTimestamp() {
        return reportTimestamp;
    }

    public String getServer() {
        return server;
    }

    public String getPlayerReported() {
        return playerReported;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSovled() {
        return sovled;
    }

    public String getSolver() {
        return solver;
    }

    public long getSolvedTimestamp() {
        return solvedTimestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        DataSource.UPDATE_STRING(getId(), "player_name", playerName);
    }

    public void setReportTimestamp(long reportTimestamp) {
        this.reportTimestamp = reportTimestamp;
        DataSource.UPDATE_LONG(getId(), "report_timestamp", reportTimestamp);
    }

    public void setServer(String server) {
        this.server = server;
        DataSource.UPDATE_STRING(getId(), "server", server);
    }

    public void setPlayerReported(String playerReported) {
        this.playerReported = playerReported;
        DataSource.UPDATE_STRING(getId(), "player_reported", playerReported);
    }

    public void setMessage(String message) {
        this.message = message;
        DataSource.UPDATE_STRING(getId(), "message", message);
    }

    public void setSovled(boolean sovled) {
        this.sovled = sovled;
        DataSource.UPDATE_BOOLEAN(getId(), "solved", sovled);
    }

    public void setSolver(String solver) {
        this.solver = solver;
        DataSource.UPDATE_STRING(getId(), "solver", solver);
    }

    public void setSolvedTimestamp(long solvedTimestamp) {
        this.solvedTimestamp = solvedTimestamp;
        DataSource.UPDATE_LONG(getId(), "solved_timestamp", solvedTimestamp);
    }

    public static List<Report> getReports() {
        return reports;
    }
}
