package com.fteams.sstrain.entities;

import java.util.List;

public class Beatmap implements Comparable<Beatmap>{
    public Metadata metadata;
    public List<Note> notes;

    @Override
    public int compareTo(Beatmap o) {
        if (metadata == null)
            return 1;
        if (o.metadata == null)
            return -1;
        return metadata.compareTo(o.metadata);
    }

    public String toString()
    {
        String tag = "";
        if(metadata.difficultyName == null){
            tag = "" + metadata.difficulty + "*";
        }else{
            //tag = String.format("%-15s [ Lv %-3d ]", metadata.difficultyName, metadata.difficulty);
            tag = String.format("%s   [ Lv %-3d ]", metadata.difficultyName, metadata.difficulty);
        }
        return tag;
    }

}
