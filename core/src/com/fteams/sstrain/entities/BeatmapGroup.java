package com.fteams.sstrain.entities;

import com.badlogic.gdx.utils.Array;
import com.fteams.sstrain.config.GlobalConfiguration;
import com.fteams.sstrain.util.SongUtils;

public class BeatmapGroup implements Comparable<BeatmapGroup>{
    public BaseMetadata metadata;
    public Array<Beatmap> beatmaps;

    public String toString()
    {
        return "["+SongUtils.getAttribute(metadata.attribute) + "] "+ metadata.songName.replaceAll("\\\\n", " ");
    }

    @Override
    public int compareTo(BeatmapGroup o) {
        int sign = 1;
        if (GlobalConfiguration.sortOrder == SongUtils.SORTING_MODE_DESCENDING) {
            sign = -1;
        }
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_FILE_NAME)
            return metadata.songFile.compareTo(o.metadata.songFile)*sign;
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_NAME)
            return metadata.songName.compareTo(o.metadata.songName)*sign;
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_ATTRIBUTE)
            return metadata.attribute.compareTo(o.metadata.attribute)*sign;
        if (GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_SONG_ID)
            return (metadata.id.intValue() - o.metadata.id.intValue())*sign;
        if(GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_DEB_LV)
            return (this.getDifficultyLv(1) - o.getDifficultyLv(1))*sign;
        if(GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_REG_LV)
            return (this.getDifficultyLv(2) - o.getDifficultyLv(2))*sign;
        if(GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_PRO_LV)
            return (this.getDifficultyLv(3) - o.getDifficultyLv(3))*sign;
        if(GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_MAS_LV)
            return (this.getDifficultyLv(4) - o.getDifficultyLv(4))*sign;
        if(GlobalConfiguration.sortMode == SongUtils.SORTING_MODE_MASPLUS_LV)
            return (this.getDifficultyLv(5) - o.getDifficultyLv(5))*sign;
        return sign*Double.compare(metadata.duration, o.metadata.duration);
    }

    public int getDifficultyLv(int difficultyID){
        // difficultyID = 1,2,3,4,5 for debut,reg,pro,mas,mas+
        if(difficultyID < 1 || difficultyID >= SongUtils.difficultyLvRange.length)
            return 0;
        // I don't want to standardize difficultyName, so I use Lv range to determine difficulty
        for(Beatmap beatmap: beatmaps){
            int lv = beatmap.metadata.difficulty;
            if(lv >=SongUtils.difficultyLvRange[difficultyID-1] && lv < SongUtils.difficultyLvRange[difficultyID]){
                return lv;
            }
        }
        return 0;
    }
}
