package org.mtr.mod;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.registry.EntityTypeRegistryObject;
import org.mtr.mapping.registry.Registry;
import org.mtr.mod.entity.EntityRendering;

public final class EntityTypes {

	static {
		RENDERING = Registry.registerEntityType(new Identifier(Init.MOD_ID, "rendering"), EntityRendering::new, 0.01F, 0.01F);
	}

	public static final EntityTypeRegistryObject<EntityRendering> RENDERING;

	public static void init() {
		Init.LOGGER.info("Registering Minecraft Transit Railway entity types");
	}
}