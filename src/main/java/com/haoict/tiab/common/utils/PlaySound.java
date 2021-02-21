package com.haoict.tiab.common.utils;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaySound {
  public static void playNoteBlockHarpSound(World world, BlockPos pos, String note) {
    // https://minecraft.gamepedia.com/Note_Block
    switch (note) {
      // Octave 1
      case "F#":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.5F);
        break;
      case "G":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.529732F);
        break;
      case "G#":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.561231F);
        break;
      case "A":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.594604F);
        break;
      case "A#":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.629961F);
        break;
      case "B":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.667420F);
        break;
      case "C":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.707107F);
        break;
      case "C#":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.749154F);
        break;
      case "D":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.793701F);
        break;
      case "D#":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.840896F);
        break;
      case "E":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.890899F);
        break;
      case "F":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.943874F);
        break;

      // Octave 2
      case "F#2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1F);
        break;
      case "G2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.059463F);
        break;
      case "G#2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.122462F);
        break;
      case "A2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.189207F);
        break;
      case "A#2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.259921F);
        break;
      case "B2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.334840F);
        break;
      case "C2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.414214F);
        break;
      case "C#2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.498307F);
        break;
      case "D2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.587401F);
        break;
      case "D#2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.681793F);
        break;
      case "E2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.781797F);
        break;
      case "F2":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.887749F);
        break;

      case "F#3":
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 2F);
        break;
    }
  }
}
