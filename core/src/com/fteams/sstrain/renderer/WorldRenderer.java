package com.fteams.sstrain.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.fteams.sstrain.World;
import com.fteams.sstrain.assets.Assets;
import com.fteams.sstrain.config.GlobalConfiguration;
import com.fteams.sstrain.entities.Note;
import com.fteams.sstrain.objects.AccuracyMarker;
import com.fteams.sstrain.objects.AccuracyPopup;
import com.fteams.sstrain.objects.Circle;
import com.fteams.sstrain.objects.TapZone;
import com.fteams.sstrain.util.Accuracy;
import com.fteams.sstrain.util.SongUtils;


public class WorldRenderer {

    private static final float CAMERA_WIDTH = 600f;
    private static final float CAMERA_HEIGHT = 400f;

    private World world;
    private OrthographicCamera cam;
    private float height_to_circle_ratio = 0.14f;
    // textures
    TextureRegion circle;
    TextureRegion circleSim;
    TextureRegion circleHold;
    TextureRegion circleSwipeLeft;
    TextureRegion circleSwipeLeftSim;
    TextureRegion circleSwipeRight;
    TextureRegion circleSwipeRightSim;

    TextureRegion tapZoneIdle;
    TextureRegion tapZoneWarn;
    TextureRegion tapZonePressed;

    TextureRegion tapZoneAll;
    TextureRegion tapZoneCute;
    TextureRegion tapZoneCool;
    TextureRegion tapZonePassion;

    TextureRegion accBadBackground;
    TextureRegion accGoodBackground;
    TextureRegion accGreatBackground;
    TextureRegion accPerfectBackground;

    TextureRegion holdBG;

    TextureRegion accHitMark;

    TextureRegion missMark;
    TextureRegion badLateMark;
    TextureRegion badSoonMark;
    TextureRegion goodLateMark;
    TextureRegion goodSoonMark;
    TextureRegion greatLateMark;
    TextureRegion greatSoonMark;
    TextureRegion perfectMark;

    TextureRegion comboMark;

    BitmapFont font;
    BitmapFont songFont;

    GlyphLayout layout;

    // extra stuff
    private PolygonSpriteBatch spriteBatch;

    private ShapeRenderer renderer;

    private int width;
    private int height;
    private int positionOffsetX;
    private int positionOffsetY;
    // pixels per unit on X
    public float ppuX;
    // pixels per unit on Y
    public float ppuY;

    private float time;

    short[] triangles = {0, 1, 2, 0, 2, 3};

    public void setSize(int w, int h, int offsetX, int offsetY) {
        this.width = w;
        this.height = h;
        this.positionOffsetX = offsetX;
        this.positionOffsetY = offsetY;
        ppuX = (float) width / CAMERA_WIDTH;
        ppuY = (float) height / CAMERA_HEIGHT;
    }

    public WorldRenderer(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.position.set(0f, 0f, 0f);
        this.cam.update();
        spriteBatch = new PolygonSpriteBatch();
        renderer = new ShapeRenderer();
        layout = new GlyphLayout();
        loadTextures();
    }

    private void loadTextures() {
        TextureAtlas atlas = Assets.atlas;
        circle = atlas.findRegion("circle");
        circleHold = atlas.findRegion("circle_hold");
        circleSim = atlas.findRegion("circle_sim");
        circleSwipeLeft = atlas.findRegion("circle_swipe_left");
        circleSwipeLeftSim = atlas.findRegion("circle_swipe_left_sim");
        circleSwipeRight = atlas.findRegion("circle_swipe_right");
        circleSwipeRightSim = atlas.findRegion("circle_swipe_right_sim");

        tapZoneIdle = atlas.findRegion("tap");
        tapZonePressed = atlas.findRegion("tap_pressed");
        tapZoneWarn = atlas.findRegion("tap_warn");
        //
        tapZoneAll = atlas.findRegion("tap_zone_all");
        tapZoneCute = atlas.findRegion("tap_zone_cute");
        tapZoneCool = atlas.findRegion("tap_zone_cool");
        tapZonePassion = atlas.findRegion("tap_zone_passion");

        accBadBackground = atlas.findRegion("acc_bad");
        accGoodBackground = atlas.findRegion("acc_good");
        accGreatBackground = atlas.findRegion("acc_great");
        accPerfectBackground = atlas.findRegion("acc_perfect");
        accHitMark = atlas.findRegion("acc_mark");

        holdBG = new TextureRegion(Assets.holdBG);

        missMark = atlas.findRegion("miss");
        badLateMark = atlas.findRegion("bad_late");
        badSoonMark = atlas.findRegion("bad_soon");
        goodLateMark = atlas.findRegion("nice_late");
        goodSoonMark = atlas.findRegion("nice_soon");
        greatLateMark = atlas.findRegion("great_late");
        greatSoonMark = atlas.findRegion("great_soon");
        perfectMark = atlas.findRegion("perfect");

        comboMark = atlas.findRegion("combo_mark");

        font = Assets.font;
        songFont = Assets.songFont;
    }

    public void render() {
        spriteBatch.begin();
        renderer.setProjectionMatrix(cam.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        drawTapZones();
        drawCircles();
        if (GlobalConfiguration.displayLine) {
            drawFlatBar();
        }
        drawCombo();
        //drawProgressBar();
        drawAccuracyBar();
        if (!world.started) {
            drawTapToBeginMessage();
        }
        if (!world.paused) {
            drawAccuracy();
        }
        if (world.paused) {
            drawTapToContinue();
        }
        renderer.end();
        spriteBatch.end();
        time += Gdx.graphics.getDeltaTime();
    }

    private void drawFlatBar() {
        float centerX = this.positionOffsetX + width / 2;
        float y = this.positionOffsetY + height - height * 0.72f;
        spriteBatch.draw(accBadBackground, centerX - width / 2f, y, width, height * 0.01f);
    }

    private void drawAccuracyBar() {
        float centerX = this.positionOffsetX + width / 2;
        float y = this.positionOffsetY + height - height * 0.1f;
        float bad = (float) (SongUtils.overallDiffBad[GlobalConfiguration.overallDifficulty] * 1f);
        float nice = (float) (SongUtils.overallDiffNice[GlobalConfiguration.overallDifficulty] * 1f);
        float great = (float) (SongUtils.overallDiffGreat[GlobalConfiguration.overallDifficulty] * 1f);
        float perfect = (float) (SongUtils.overallDiffPerfect[GlobalConfiguration.overallDifficulty] * 1f);
        float zone = bad / 1000f;
        // draw the background (bad level)
        spriteBatch.draw(accBadBackground, centerX - width / 6f, y, width / 3f, height * 0.01f);
        // draw the background (good level)
        spriteBatch.draw(accGoodBackground, centerX - nice / bad * width / 6f, y, nice / bad * width / 3f, height * 0.01f);
        // draw the background (great level)
        spriteBatch.draw(accGreatBackground, centerX - great / bad * width / 6f, y, great / bad * width / 3f, height * 0.01f);
        // draw the background (perfect level)
        spriteBatch.draw(accPerfectBackground, centerX - perfect / bad * width / 6f, y, perfect / bad * width / 3f, height * 0.01f);
        // draw each of the 'markers'
        for (AccuracyMarker accMarker : world.getAccuracyMarkers()) {
            if (accMarker.display) {

                spriteBatch.setColor(1, 1, 1, accMarker.getAlpha());
                spriteBatch.draw(accHitMark, centerX + (accMarker.getTime()) * (width / 6f) / zone - accHitMark.getRegionWidth(), y - height * 0.01f, 3f, height * 0.03f);
            }
        }
        spriteBatch.setColor(1, 1, 1, 1);
    }

    private void drawTapToBeginMessage() {
        String tapToBegin = "Tap to begin!" + (GlobalConfiguration.playbackMode != null && GlobalConfiguration.playbackMode.equals(SongUtils.GAME_MODE_ABREPEAT) ? " To exit in A-B Repeat Mode, tap back twice." : "");
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        layout.setText(songFont, tapToBegin);
        songFont.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);
    }

    private void drawTapToContinue() {
        String tapToBegin = "Tap to continue!";
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        layout.setText(songFont, tapToBegin);
        songFont.draw(spriteBatch, tapToBegin, centerX - layout.width / 2, centerY - layout.height / 2);

        String backToExit = "Or press back again to skip to the Results screen.";
        centerX = this.positionOffsetX + width / 2;
        centerY = this.positionOffsetY + height / 2 + height * 0.1f;
        layout.setText(songFont, backToExit);
        songFont.draw(spriteBatch, backToExit, centerX - layout.width / 2, centerY - layout.height / 2);
    }

    private void drawAccuracy() {
        float scale = height / GlobalConfiguration.BASE_HEIGHT;
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height / 2 + height * 0.15f;
        for (AccuracyPopup popup : world.getAccuracyPopups()) {
            if (popup.show) {
                TextureRegion region = perfectMark;
                if (popup.accuracy == Accuracy.MISS) {
                    region = missMark;
                }
                if (popup.accuracy == Accuracy.BAD) {
                    region = popup.soon ? badSoonMark : badLateMark;
                }
                if (popup.accuracy == Accuracy.NICE) {
                    region = popup.soon ? goodSoonMark : goodLateMark;
                }
                if (popup.accuracy == Accuracy.GREAT) {
                    region = popup.soon ? greatSoonMark : greatLateMark;
                }
                spriteBatch.setColor(1, 1, 1, popup.getAlpha());
                spriteBatch.draw(region, centerX - scale * region.getRegionWidth() * popup.getSize() / 2, centerY - scale * region.getRegionHeight() * popup.getSize() / 2, scale * region.getRegionWidth() * popup.getSize(), scale * region.getRegionHeight() * popup.getSize());
            }
        }
        spriteBatch.setColor(1, 1, 1, 1);
    }

    private void drawCombo() {
        float centerX = this.positionOffsetX + width * 4/ 5;
        float centerY = height * 4 / 5;
        if (world.combo != 0) {
            String str = "" + world.combo;
            //layout.setText(font, str);
            layout.setText(font, str, 0, str.length(), font.getColor(), 0, Align.center, false, null);
            float x = centerX - layout.width / 2;
            float y = centerY - layout.height / 2;
            font.draw(spriteBatch, "" + world.combo, x, y);

            TextureRegion region = comboMark;
            float markHeight = height*0.05f;
            float markWidth  = markHeight*comboMark.getRegionWidth()/comboMark.getRegionHeight();
            spriteBatch.draw(region, x, y - layout.height*2,markWidth,markHeight);
        }
    }

    private void drawTapZones() {
        // implement different trZone for different attribute in the future
        TextureRegion trZone = tapZoneAll;

        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height - height * 0.2f;
        float size  = height * 0.125f *1.44f;//* 1.42f;
        float width = trZone.getRegionWidth()*size/trZone.getRegionHeight();
        TapZone zone = world.getTapZones().get(0);
        float x = centerX - width / 2;
        float y = centerY + zone.getPosition().y * ppuY - size / 2;
        // draw tap zone
        spriteBatch.draw(trZone, x, y, width, size);

        for (TapZone tapZone : world.getTapZones()) {

            TextureRegion region = tapZoneIdle;

            if (tapZone.warn) {
                region = tapZoneWarn;
            }

            if (tapZone.pressed) {
                tapZone.touchTime = time;
            }

            x = centerX + tapZone.getPosition().x * ppuX - size / 2;
            y = centerY + tapZone.getPosition().y * ppuY - size / 2;

            // no longer draw 5 circles on tap zone
            //spriteBatch.draw(region, x, y, size, size);

            float alpha = 1f - MathUtils.clamp((time - tapZone.touchTime) * 5f, 0f, 1f);
            if (alpha > 0) {
                Color c = spriteBatch.getColor();
                spriteBatch.setColor(c.r, c.g, c.b, Interpolation.pow2In.apply(alpha));
                spriteBatch.draw(tapZonePressed, x, y, size, size);
                spriteBatch.setColor(c);
            }
        }

    }

    private void drawCircles() {
        float centerX = this.positionOffsetX + width / 2;
        float centerY = this.positionOffsetY + height - height * 0.2f;
        float size = height * height_to_circle_ratio;
        // calculate width,height based on texture width and height
        float cir_height = height * height_to_circle_ratio;
        float cir_width  = height * height_to_circle_ratio * circle.getRegionWidth()/circle.getRegionHeight();

        Array<Circle> circles = world.getCircles();
        // draw in reverse order
        for (int i=circles.size-1;i>=0;i--) {
            Circle mark = circles.get(i);
            if (!mark.visible)
                continue;

            float alpha = mark.alpha;
            // float alpha2 = mark.alpha2;
            Color c = spriteBatch.getColor();

            if (mark.holding)
                spriteBatch.setColor(1.0f, 1.0f, 0.5f, alpha * 1f * 0.45f * (0.75f + 0.25f * MathUtils.sin(time * 7f + mark.hitTime)));
            else
                spriteBatch.setColor(c.r, c.g, c.b, alpha * alpha * 1f * 0.45f);

            if (mark.nextNote != null && !mark.nextNote.isDone()) {
                Vector2 org = mark.nextNote.position.cpy();
                org.x *= ppuX;
                org.y *= ppuY;
                org.x += centerX;
                org.y += centerY;

                Vector2 dst = mark.position.cpy();
                dst.x *= ppuX;
                if (mark.holding) {
                    dst.y = -249f;
                }

                dst.y *= ppuY;
                dst.x += centerX;
                dst.y += centerY;
                if(mark.parabolic && mark.note.type==SongUtils.NOTE_TYPE_HOLD && mark.note.status==0){
                    // hold beam for parabolic fall
                    drawHoldParabola(mark, org, dst, size);
                }else {
                    drawHoldBeam(org, dst, size * mark.nextNote.size, size * mark.size);
                }
            }

            if (!mark.note.sync.equals(0L)) {
                Circle mark2 = mark.nextSyncNote;
                // we only check ahead if we  got the same note, we don't want overlapping beams
                // draw beams only if neither note has been ever hit
                if (mark2 != null && mark.accuracy == null && mark2.accuracy == null) {
                    Vector2 org = mark2.position.cpy();
                    org.x *= ppuX;
                    org.y *= ppuY;
                    org.x += centerX;
                    org.y += centerY;

                    Vector2 dst = mark.position.cpy();
                    dst.x *= ppuX;
                    dst.y *= ppuY;
                    dst.x += centerX;
                    dst.y += centerY;
                    drawHoldBeam(org, dst, size * 0.1f, size * 0.1f);
                }
            }
            if (mark.visible) {
                float this_width = cir_width* mark.size;
                float this_height = cir_height * mark.size;
                spriteBatch.setColor(c.r, c.g, c.b, alpha);
                spriteBatch.draw(selectTextureForCircle(mark.note), centerX - this_width / 2 + mark.position.x * ppuX, centerY - this_height / 2 + mark.position.y * ppuY, this_width, this_height);

            }
            spriteBatch.setColor(c);
        }

    }

    private void drawHoldBeam(Vector2 from, Vector2 to, float orgSize, float dstSize) {
        Vector2 delta = from.cpy().sub(to);

        float w = Math.max(orgSize, dstSize);
        float h = delta.len();

        float tw = holdBG.getRegionWidth();
        float th = holdBG.getRegionHeight();

        float factorScale = (tw / w) * 0.5f;
        float topFactor = Math.max(dstSize - orgSize, 0f) * factorScale;
        float botFactor = Math.max(orgSize - dstSize, 0f) * factorScale;

        float[] points = {
                topFactor,
                0f,

                botFactor,
                th,

                tw - botFactor,
                th,

                tw - topFactor,
                0f
        };

        PolygonRegion clamped = new PolygonRegion(holdBG, points, triangles);
        spriteBatch.draw(clamped, from.x - w * 0.5f, from.y, w * 0.5f, 0f, w, h, 1f, 1f, delta.angle() + 90);
    }
    private void drawHoldParabola(Circle mark, Vector2 from0, Vector2 to0,float size0) {
        Vector2 from;
        Vector2 to;
        float fromSize, toSize;
        from = from0.cpy();
        fromSize = mark.nextNote.size;
        // build hold beam from trajectory of the note
        for(int i=0;i<mark.nTraj;i++){
            to = mark.traj[i].cpy();
            toSize = mark.traj_size[i];
            if(to.y > from.y) {
                continue;
            }
            if(to.y < to0.y){
                break;
            }
            drawHoldBeam(from, to, fromSize*size0, toSize*size0);
            from = to.cpy();
            fromSize = toSize;
        }
        if(from.y >to0.y){
            toSize = mark.size;
            drawHoldBeam(from, to0, fromSize*size0, toSize*size0);
            if(!mark.holding && mark.nTraj < mark.maxTraj){
                // add current position and size to trajectory
                mark.traj[mark.nTraj]=to0.cpy();
                mark.traj_size[mark.nTraj] = mark.size;
                mark.nTraj++;
            }
        }
    }
    private TextureRegion selectTextureForCircle(Note note) {

        if (note.sync.intValue() == SongUtils.NOTE_SYNC_ON) {
            if (note.status.equals(SongUtils.NOTE_NO_SWIPE)) {
                if(note.type.equals(SongUtils.NOTE_TYPE_HOLD)){
                    return circleHold;
                }else{
                    return circleSim;
                }
            } else if (note.status.equals(SongUtils.NOTE_SWIPE_LEFT)) {
                return circleSwipeLeftSim;
            } else if (note.status.equals(SongUtils.NOTE_SWIPE_RIGHT)) {
                return circleSwipeRightSim;
            }
        } else if (note.sync.intValue() == SongUtils.NOTE_SYNC_OFF) {
            if (note.status.equals(SongUtils.NOTE_NO_SWIPE)) {
                if(note.type.equals(SongUtils.NOTE_TYPE_HOLD)){
                    return circleHold;
                }else{
                    return circle;
                }
            } else if (note.status.equals(SongUtils.NOTE_SWIPE_LEFT)) {
                return circleSwipeLeft;
            } else if (note.status.equals(SongUtils.NOTE_SWIPE_RIGHT)) {
                return circleSwipeRight;
            }
        }
        return circle;
    }
}
