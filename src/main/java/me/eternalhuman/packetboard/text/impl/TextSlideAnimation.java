package me.eternalhuman.packetboard.text.impl;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.eternalhuman.packetboard.text.FrameIterator;
import me.eternalhuman.packetboard.text.TextFrame;
import me.eternalhuman.packetboard.text.TextIterator;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class TextSlideAnimation extends TextIterator {

    private final String text;
    private final ChatColor color;
    private final String colorCodes;
    private final SlideDirection direction;
    private final int frameDuration;

    @Delegate(types = {FrameIterator.class})
    private final FrameIterator frameIterator;

    public enum SlideDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public TextSlideAnimation(@NonNull String text) {
        this(text, ChatColor.WHITE, SlideDirection.LEFT_TO_RIGHT, 2);
    }

    public TextSlideAnimation(
            @NonNull String text,
            @NonNull ChatColor color,
            @NonNull SlideDirection direction,
            int frameDuration) {
        Preconditions.checkArgument(!text.isEmpty(), "Text cannot be empty");
        Preconditions.checkArgument(frameDuration > 0, "Frame duration must be positive");

        this.text = ChatColor.stripColor(text);
        this.color = color;
        this.colorCodes = ChatColor.getLastColors(text);
        this.direction = direction;
        this.frameDuration = frameDuration;
        this.frameIterator = new FrameIterator(createAnimationFrames());
    }

    @Override
    public String next() {
        return frameIterator.next();
    }

    @Override
    protected void start(List<TextFrame> frames) {
        // empty frame before animation
        frames.add(TextFrame.of("", 10));
    }

    @Override
    protected void end(List<TextFrame> frames) {
        // hold full text
        frames.add(TextFrame.of(color + colorCodes + text, 3 * 20));
    }

    private List<TextFrame> createAnimationFrames() {
        List<TextFrame> frames = new ArrayList<>();

        start(frames);

        if (direction == SlideDirection.LEFT_TO_RIGHT) {
            for (int i = 1; i <= text.length(); i++) {
                String visibleText = text.substring(0, i);
                frames.add(TextFrame.of(color + colorCodes + visibleText, frameDuration));
            }
        } else {
            for (int i = 1; i <= text.length(); i++) {
                String visibleText = text.substring(text.length() - i);
                frames.add(TextFrame.of(color + colorCodes + visibleText, frameDuration));
            }
        }

        end(frames);

        return frames;
    }
}