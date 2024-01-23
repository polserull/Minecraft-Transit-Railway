package org.mtr.mod.packet;

import org.mtr.libraries.it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.registry.PacketHandler;
import org.mtr.mod.block.BlockRailwaySignBottom;
import org.mtr.mod.block.BlockRouteSignBase;

public final class PacketUpdateRailwaySignBottomConfig extends PacketHandler {

	private final BlockPos blockPos;
	private final LongAVLTreeSet selectedIds;
	private final String[] signIds;

	public PacketUpdateRailwaySignConfig(PacketBuffer packetBuffer) {
		blockPos = packetBuffer.readBlockPos();
		final int selectedIdsLength = packetBuffer.readInt();
		selectedIds = new LongAVLTreeSet();
		for (int i = 0; i < selectedIdsLength; i++) {
			selectedIds.add(packetBuffer.readLong());
		}
		final int signLength = packetBuffer.readInt();
		signIds = new String[signLength];
		for (int i = 0; i < signLength; i++) {
			final String signId = readString(packetBuffer);
			signIds[i] = signId.isEmpty() ? null : signId;
		}
	}

	public PacketUpdateRailwaySignConfig(BlockPos blockPos, LongAVLTreeSet selectedIds, String[] signIds) {
		this.blockPos = blockPos;
		this.selectedIds = selectedIds;
		this.signIds = signIds;
	}

	@Override
	public void write(PacketBuffer packetBuffer) {
		packetBuffer.writeBlockPos(blockPos);
		packetBuffer.writeInt(selectedIds.size());
		selectedIds.forEach(packetBuffer::writeLong);
		packetBuffer.writeInt(signIds.length);
		for (final String signType : signIds) {
			packetBuffer.writeString(signType == null ? "" : signType);
		}
	}

	@Override
	public void runServerQueued(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
		final BlockEntity entity = serverPlayerEntity.getEntityWorld().getBlockEntity(blockPos);
		if (entity != null) {
			if (entity.data instanceof BlockRailwaySignBottom.BlockEntity) {
				((BlockRailwaySignBottom.BlockEntity) entity.data).setData(selectedIds, signIds);
			} else if (entity.data instanceof BlockRouteSignBase.BlockEntityBase) {
				final long platformId = selectedIds.isEmpty() ? 0 : (long) selectedIds.toArray()[0];
				((BlockRouteSignBase.BlockEntityBase) entity.data).setPlatformId(platformId);
				final BlockEntity entityAbove = serverPlayerEntity.getEntityWorld().getBlockEntity(blockPos.up());
				if (entityAbove != null && entityAbove.data instanceof BlockRouteSignBase.BlockEntityBase) {
					((BlockRouteSignBase.BlockEntityBase) entityAbove.data).setPlatformId(platformId);
				}
			}
		}
	}
}
