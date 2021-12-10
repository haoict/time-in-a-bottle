package com.haoict.tiab.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public class PlaySound {
    public static void playNoteBlockHarpSound(Level level, BlockPos pos, String note) {
        // https://minecraft.gamepedia.com/Note_Block
        switch (note) {
            // Octave 1
            case "F#":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.5F);
                break;
            case "G":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.529732F);
                break;
            case "G#":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.561231F);
                break;
            case "A":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.594604F);
                break;
            case "A#":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.629961F);
                break;
            case "B":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.667420F);
                break;
            case "C":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.707107F);
                break;
            case "C#":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.749154F);
                break;
            case "D":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.793701F);
                break;
            case "D#":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.840896F);
                break;
            case "E":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.890899F);
                break;
            case "F":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 0.943874F);
                break;

            // Octave 2
            case "F#2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1F);
                break;
            case "G2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.059463F);
                break;
            case "G#2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.122462F);
                break;
            case "A2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.189207F);
                break;
            case "A#2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.259921F);
                break;
            case "B2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.334840F);
                break;
            case "C2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.414214F);
                break;
            case "C#2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.498307F);
                break;
            case "D2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.587401F);
                break;
            case "D#2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.681793F);
                break;
            case "E2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.781797F);
                break;
            case "F2":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 1.887749F);
                break;

            case "F#3":
                level.playSound(null, pos, SoundEvents.NOTE_BLOCK_HARP, SoundSource.BLOCKS, 0.5F, 2F);
                break;
        }
    }
}
