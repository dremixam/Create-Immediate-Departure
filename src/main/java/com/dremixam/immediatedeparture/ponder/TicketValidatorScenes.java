package com.dremixam.immediatedeparture.ponder;

import net.createmod.catnip.math.Pointing;
//? if fabric {
/*import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
*///? }
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.ParrotElement;
import net.createmod.ponder.api.element.ParrotPose;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.dremixam.immediatedeparture.validator.ImmediateDepartureBlocks;

/**
 * Ponder scene(s) for the Ticket Validator, registered by {@link ImmediateDeparturePonderPlugin}.
 * Coordinates match the captured structure at {@code
 * assets/create_immediate_departure/ponder/ticket_validator.nbt}.
 */
//? if fabric {
/*@Environment(EnvType.CLIENT)
*///? }
public class TicketValidatorScenes {
    private static final BlockPos STATION = new BlockPos(8, 1, 3);
    private static final BlockPos VALIDATOR = new BlockPos(6, 1, 3);
    private static final BlockPos TRAIN_MIN = new BlockPos(5, 2, 5);
    private static final BlockPos TRAIN_MAX = new BlockPos(9, 3, 7);
    private static final BlockPos SEAT = new BlockPos(6, 3, 6);

    public static void linking(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("ticket_validator", "Setting up a Ticket Validator");
        scene.configureBasePlate(1, 0, 12);

        Selection floor = util.select().layer(0);
        Selection track = util.select().fromTo(0, 1, 6, 13, 1, 6);
        Selection stationSel = util.select().position(STATION);
        Selection validatorSel = util.select().position(VALIDATOR);
        Selection trainSel = util.select().fromTo(TRAIN_MIN, TRAIN_MAX);
        Selection wholeScene = floor.add(track)
            .add(stationSel)
            .add(validatorSel);

        scene.world().showSection(floor, Direction.UP);
        scene.idle(10);

        scene.world().showSection(track.add(stationSel), Direction.DOWN);
        scene.idle(20);

        scene.overlay().showText(80)
            .attachKeyFrame()
            .text("A Ticket Validator lets you fast-travel to any station you've discovered, right from wherever it's placed")
            .pointAt(util.vector().topOf(STATION))
            .placeNearTarget();
        scene.idle(90);

        // Beat 1: arm the item on the station.
        Object stationHighlight = new Object();
        scene.overlay().showOutline(PonderPalette.OUTPUT, stationHighlight, stationSel, 80);
        ItemStack validatorItem = ImmediateDepartureBlocks.TICKET_VALIDATOR_ITEM.get().getDefaultInstance();
        scene.overlay().showControls(util.vector().topOf(STATION), Pointing.DOWN, 60)
            .withItem(validatorItem)
            .rightClick();
        scene.idle(20);
        scene.overlay().showText(70)
            .attachKeyFrame()
            .text("Right-click the station you want to link it to...")
            .pointAt(util.vector().topOf(STATION))
            .colored(PonderPalette.OUTPUT)
            .placeNearTarget();
        scene.idle(70);

        // Beat 2: the validator itself appears, timed as if just placed.
        scene.world().showSection(validatorSel, Direction.SOUTH);
        scene.idle(15);
        scene.overlay().showText(80)
            .attachKeyFrame()
            .text("...then place it anywhere within range")
            .pointAt(util.vector().topOf(VALIDATOR))
            .colored(PonderPalette.INPUT)
            .placeNearTarget();
        scene.idle(80);

        // Beat 3: the train rolls out with a parrot riding along.
        scene.overlay().showText(70)
            .text("Once your stations are connected by rail...")
            .pointAt(util.vector().topOf(TRAIN_MIN))
            .placeNearTarget();

        Vec3 seatTop = util.vector().centerOf(SEAT);
        ElementLink<ParrotElement> parrot = scene.special().createBirb(seatTop, ParrotPose.FacePointOfInterestPose::new);
        Vec3 parrotPos = seatTop;
        ElementLink<WorldSectionElement> train = scene.world().showIndependentSection(trainSel, Direction.DOWN);
        scene.idle(20);

        Vec3 legToStation = new Vec3(3, 0, 0);
        scene.world().moveSection(train, legToStation, 25);
        scene.special().moveParrot(parrot, legToStation, 25);
        parrotPos = parrotPos.add(legToStation);
        scene.idle(25);
        scene.overlay().showText(70)
            .attachKeyFrame()
            .text("...and a Ticket Validator lets players get there instantly, without waiting for a ride")
            .pointAt(util.vector().topOf(STATION))
            .placeNearTarget();
        scene.world().moveSection(train, legToStation, 25);
        scene.special().moveParrot(parrot, legToStation, 25);
        parrotPos = parrotPos.add(legToStation);
        scene.idle(45);

        // Train reaches the end of the line and vanishes.
        scene.world().hideIndependentSection(train, Direction.DOWN);
        Vec3 parkedBelow = parrotPos.add(0, -30, 0);
        scene.special().moveParrot(parrot, parkedBelow.subtract(parrotPos), 0);
        parrotPos = parkedBelow;
        scene.idle(5);

        // Scene cuts out and back in, minus the train, to suggest a jump elsewhere.
        scene.world().hideSection(wholeScene, Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(wholeScene, Direction.DOWN);
        scene.idle(10);

        // Train reappears at the opposite end of the track, arriving this time.
        ElementLink<WorldSectionElement> train2 = scene.world().showIndependentSection(trainSel, Direction.DOWN);
        scene.world().moveSection(train2, new Vec3(-5, 0, 0), 0);
        Vec3 seatArrival = seatTop.add(-5, 0, 0);
        scene.special().moveParrot(parrot, seatArrival.subtract(parrotPos), 0);
        parrotPos = seatArrival;
        scene.overlay().showText(70)
            .attachKeyFrame()
            .text("...to arrive somewhere else entirely")
            .pointAt(util.vector().topOf(STATION))
            .placeNearTarget();
        scene.idle(20);

        // Beat 4: the train rolls back in to its resting spot and stops.
        Vec3 legToRest = new Vec3(5, 0, 0);
        scene.world().moveSection(train2, legToRest, 40);
        scene.special().moveParrot(parrot, legToRest, 40);
        parrotPos = parrotPos.add(legToRest);
        scene.idle(50);

        // The parrot hops off in front of the validator.
        Vec3 villagerSpot = util.vector().topOf(VALIDATOR.below())
            .add(0, 0, -1);
        scene.special().moveParrot(parrot, villagerSpot.subtract(parrotPos), 10);
        parrotPos = villagerSpot;
        scene.idle(10);
        scene.overlay().showText(70)
            .text("Each new station a player visits becomes available for fast-travel afterwards")
            .pointAt(villagerSpot)
            .placeNearTarget();
        scene.idle(60);

        // Train continues on, empty.
        scene.world().moveSection(train2, new Vec3(6, 0, 0), 40);
        scene.idle(45);
        scene.world().hideIndependentSection(train2, Direction.DOWN);

        // Beat 5: right-click the validator to reach a discovered station instantly.
        Object validatorHighlight = new Object();
        scene.overlay().showOutline(PonderPalette.INPUT, validatorHighlight, validatorSel, 60);
        scene.overlay().showControls(util.vector().topOf(VALIDATOR), Pointing.DOWN, 60)
            .rightClick();
        scene.idle(20);
        scene.overlay().showText(90)
            .attachKeyFrame()
            .text("Right-click it to instantly reach any discovered station")
            .pointAt(util.vector().topOf(VALIDATOR))
            .placeNearTarget();
        scene.idle(30);

        scene.special().moveParrot(parrot, new Vec3(0, -30, 0), 0);

        ParticleEmitter teleportEffect = (level, x, y, z) -> {
            for (int i = 0; i < 5; i++) {
                double angle = Math.random() * Math.PI * 2;
                double radius = 0.2 + Math.random() * 0.5;
                double dx = Math.cos(angle) * radius;
                double dz = Math.sin(angle) * radius;
                double dy = 0.3 + Math.random() * 0.5;
                level.addParticle(ParticleTypes.REVERSE_PORTAL, x, y, z, dx, dy, dz);
            }
        };
        scene.effects().emitParticles(villagerSpot, teleportEffect, 4, 20);
        scene.idle(60);
    }
}
