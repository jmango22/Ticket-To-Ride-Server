package edu.goldenhammer.model;

import java.util.List;

/**
 * Created by devonkinghorn on 3/25/17.
 */
public class TrackForest {
    public class TrackTree {
        List<Track> tracks;
        int playerNumber;
        public TrackTree(List<Track> tracks, int playerNumber) {
            this.tracks = tracks;
            this.playerNumber = playerNumber;
        }

        public int getPlayerNumber() {
            return playerNumber;
        }

        public void setPlayerNumber(int playerNumber) {
            this.playerNumber = playerNumber;
        }

        public List<Track> getTracks() {
            return tracks;
        }

        public void setTracks(List<Track> tracks) {
            this.tracks = tracks;
        }
    }
    List<TrackTree> trees;

    public TrackForest(List<Track> tracks) {

    }

    public List<TrackTree> getTrees() {
        return trees;
    }
}
