package wayoftime.bloodmagic.ritual.types;

import java.util.function.Consumer;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import wayoftime.bloodmagic.BloodMagic;
import wayoftime.bloodmagic.ritual.AreaDescriptor;
import wayoftime.bloodmagic.ritual.EnumRuneType;
import wayoftime.bloodmagic.ritual.IMasterRitualStone;
import wayoftime.bloodmagic.ritual.Ritual;
import wayoftime.bloodmagic.ritual.RitualComponent;
import wayoftime.bloodmagic.ritual.RitualRegister;

@RitualRegister("water")
public class RitualWater extends Ritual
{
	public static final String WATER_RANGE = "waterRange";

	public RitualWater()
	{
		super("ritualWater", 0, 500, "ritual." + BloodMagic.MODID + ".waterRitual");
		addBlockRange(WATER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1));
		setMaximumVolumeAndDistanceOfRange(WATER_RANGE, 9, 3, 3);
	}

	@Override
	public void performRitual(IMasterRitualStone masterRitualStone)
	{
		World world = masterRitualStone.getWorldObj();
		int currentEssence = masterRitualStone.getOwnerNetwork().getCurrentEssence();

		if (currentEssence < getRefreshCost())
		{
			masterRitualStone.getOwnerNetwork().causeNausea();
			return;
		}

		int maxEffects = currentEssence / getRefreshCost();
		int totalEffects = 0;

		AreaDescriptor waterRange = masterRitualStone.getBlockRange(WATER_RANGE);

		for (BlockPos newPos : waterRange.getContainedPositions(masterRitualStone.getMasterBlockPos()))
		{
			if (world.isAirBlock(newPos))
			{
				world.setBlockState(newPos, Blocks.WATER.getDefaultState());
				totalEffects++;
			}

			if (totalEffects >= maxEffects)
			{
				break;
			}
		}

		masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(getRefreshCost() * totalEffects));
	}

	@Override
	public int getRefreshTime()
	{
		return 1;
	}

	@Override
	public int getRefreshCost()
	{
		return 25;
	}

	@Override
	public void gatherComponents(Consumer<RitualComponent> components)
	{
		addCornerRunes(components, 1, 0, EnumRuneType.WATER);
	}

	@Override
	public Ritual getNewCopy()
	{
		return new RitualWater();
	}
}
