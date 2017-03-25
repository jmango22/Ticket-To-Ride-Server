package edu.goldenhammer.model;

import java.util.*;

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

        public void addTrack(Track t) {
            tracks.add(t);
        }

        public void setTracks(List<Track> tracks) {
            this.tracks = tracks;
        }

        public boolean trackConnectedToTree(Track t) {
            for(Track includedTrack: tracks) {
                if (tracksConnect(t,includedTrack)){
                    return true;
                }
            }
            return false;
        }

        private boolean tracksConnect(Track first, Track second) {
            return     first.getCity1().getName().equals(second.getCity1().getName())
                    || first.getCity1().getName().equals(second.getCity2().getName())
                    || first.getCity2().getName().equals(second.getCity1().getName())
                    || first.getCity2().getName().equals(second.getCity2().getName());
        }
    }
    List<TrackTree> trees;

    public TrackForest(List<Track> tracks) {
        generateForest(tracks);
    }

    public List<TrackTree> getTrees() {
        return trees;
    }

    public boolean connectedCities(City city1, City city2, int player_id) {
        for(TrackTree tree : trees) {
            if(tree.getPlayerNumber() == player_id) {
                boolean connect1 = false;
                boolean connect2 = false;
                for(Track track : tree.getTracks()) {
                    if(track.hasCity(city1)) {
                        connect1 = true;
                    }
                    if(track.hasCity(city2)) {
                        connect2 = true;
                    }
                }
                if(connect1 && connect2) {
                    return true;
                }
            }
        }
        return false;
    }

    private void generateForest(List<Track> tracks) {
        java.util.Map<Integer, List<Track>> playerTracks = playerToTracks(tracks);
        for(int player: playerTracks.keySet()) {
            List<Track> pList = playerTracks.get(player);
            while(!pList.isEmpty()) {
                Track t = pList.get(0);
                TrackTree tree = new TrackTree(new ArrayList<>(), player);
                tree.addTrack(t);
                pList.remove(0);
                boolean added;
                do {
                    added = false;
                    Iterator<Track> iterator = pList.iterator();
                    while(iterator.hasNext()) {
                        Track track = iterator.next();
                        if(tree.trackConnectedToTree(track)) {
                            added = true;
                            tree.addTrack(track);
                            iterator.remove();
                        }
                    }
                } while(added);
                trees.add(tree);
            }
        }
    }



    private java.util.Map<Integer, List<Track>> playerToTracks(List<Track> tracks) {
        TreeMap<Integer, List<Track>> playerTracks = new TreeMap<>();
        for (Track t: tracks) {
            int owner = t.getOwner();
            if(owner >= 0) {
                playerTracks.putIfAbsent(owner, new ArrayList<>());
                playerTracks.get(owner).add(t);
            }
        }
        return playerTracks;
    }

    private TrackForest mergeForests(TrackForest first, TrackForest second) {
        return null;
    }
}
