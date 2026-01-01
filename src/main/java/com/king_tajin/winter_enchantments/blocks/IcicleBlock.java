package com.king_tajin.winter_enchantments.blocks;

import com.king_tajin.winter_enchantments.WinterEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class IcicleBlock extends PointedDripstoneBlock {
    public static final EnumProperty<IcicleType> ICICLE_TYPE = EnumProperty.create("type", IcicleType.class);

    private static final ResourceKey<DamageType> ICICLE_SHRAPNEL = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "icicle_shrapnel")
    );

    private static final ResourceKey<DamageType> ICICLE_FALLING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(WinterEnchantments.MODID, "icicle_falling")
    );

    public IcicleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(TIP_DIRECTION, Direction.DOWN)
                .setValue(THICKNESS, DripstoneThickness.TIP)
                .setValue(WATERLOGGED, false)
                .setValue(ICICLE_TYPE, IcicleType.TIP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ICICLE_TYPE);
    }

    public enum IcicleType implements net.minecraft.util.StringRepresentable {
        TIP("tip"),
        BASE("base");

        private final String name;

        IcicleType(String name) {
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public @NonNull DamageSource getFallDamageSource(@NonNull Entity entity) {
        return new DamageSource(
                entity.level().registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ICICLE_FALLING),
                entity
        );
    }

    @Override
    public void onBrokenAfterFall(
            net.minecraft.world.level.@NonNull Level level,
            net.minecraft.core.@NonNull BlockPos pos,
            net.minecraft.world.entity.item.@NonNull FallingBlockEntity fallingBlock
    ) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            List<Entity> passengers = fallingBlock.getPassengers();
            for (Entity passenger : passengers) {
                passenger.discard();
            }

            spawnIcyExplosionParticles(serverLevel, pos);
            level.playSeededSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.5F, 0.4F, level.random.nextLong());

            level.playSeededSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.PLAYER_HURT_FREEZE, SoundSource.BLOCKS, 0.8F, 0.8F, level.random.nextLong());

            double radius = 4.0;
            AABB area = new AABB(pos).inflate(radius);
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity entity : nearbyEntities) {
                double distance = entity.position().distanceTo(pos.getCenter());
                if (distance <= radius) {
                    float damage = (float) ((10.0 * (1.0 - distance / radius)) + 5);

                    entity.hurtServer(serverLevel, createIcicleDamageSource(level, fallingBlock), damage);

                    entity.addEffect(new MobEffectInstance(
                            MobEffects.SLOWNESS,
                            100,
                            2,
                            false,
                            true,
                            true
                    ));

                    entity.setTicksFrozen(300);
                }
            }
        }
    }

    private DamageSource createIcicleDamageSource(Level level, FallingBlockEntity fallingBlock) {
        return new DamageSource(
                level.registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ICICLE_SHRAPNEL),
                fallingBlock
        );
    }

    private void spawnIcyExplosionParticles(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 15; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 3.5;
            double offsetY = level.random.nextDouble() * 2.5;
            double offsetZ = (level.random.nextDouble() - 0.5) * 3.5;

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    0, 0, 0,
                    0.25
            );
        }

        for (int i = 0; i < 40; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.5;
            double offsetY = (level.random.nextDouble() - 0.5) * 2.5;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.5;

            level.sendParticles(
                    ParticleTypes.SPLASH,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    0, 0, 0,
                    0
            );
        }

        for (int i = 0; i < 40; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 2.5;
            double offsetY = level.random.nextDouble() * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 2.5;

            level.sendParticles(
                    ParticleTypes.FALLING_WATER,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    0, -0.1, 0,
                    0.1
            );
        }
    }
}