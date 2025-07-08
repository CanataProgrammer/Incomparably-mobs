package com.incomparablymobs;

import com.incomparablymobs.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(incomparablymobs.MODID)
public class incomparablymobs {

    public static final String MODID = "incomparablymobs";

    // 移動速度を増加させるModifierのユニークIDと名前
    // UUIDはランダムなもので構いません。このIDでModifierが重複して適用されるのを防ぎます。
    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("a9c6b8a8-3f4a-4f5a-8b3a-9f2d1e9b2c1d");
    private static final String MOVEMENT_SPEED_MODIFIER_NAME = "incomparablymobs Movement Speed";


    public incomparablymobs() {
        ModLoadingContext.get().registerConfig(Type.SERVER, ModConfig.SPEC, "incomparablymobs-server.toml");
        // このクラスのイベントハンドラをForgeのイベントバスに登録します。
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * エンティティがワールドに参加したときに呼び出されるイベントハンドラ。
     * ここでモブの移動速度を強化します。
     * @param event EntityJoinLevelEvent
     */
    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // イベントからエンティティを取得し、それがMobクラスのインスタンスか確認します。
        // これにより、プレイヤーやアイテムなど、モブ以外のエンティティは対象外になります。
        if (event.getEntity() instanceof Mob mob) {

            // 移動速度の属性(Attribute)インスタンスを取得します。
            AttributeInstance movementSpeedAttribute = mob.getAttribute(Attributes.MOVEMENT_SPEED);

            // 属性がnullでないこと、そして私たちのModifierがまだ適用されていないことを確認します。
            // これにより、ディメンション移動などでイベントが再発火しても、能力が重複して上昇するのを防ぎます。
            if (movementSpeedAttribute != null && movementSpeedAttribute.getModifier(MOVEMENT_SPEED_MODIFIER_ID) == null) {
                // 移動速度を2倍にするためのModifierを作成。
                // Operation.MULTIPLY_TOTAL は、元の値に (1.0 + amount) を乗算。
                // 1.0 を指定すると、(1.0 + 1.0) = 2.0倍。
                double speedMultiplier = ModConfig.MOVEMENT_SPEED_MULTIPLIER.get() - 1.0;
                AttributeModifier speedModifier = new AttributeModifier(
                        MOVEMENT_SPEED_MODIFIER_ID,
                        MOVEMENT_SPEED_MODIFIER_NAME,
                        speedMultiplier, // 100%増加
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                );

                // Modifierを永続的に適用します。
                movementSpeedAttribute.addPermanentModifier(speedModifier);
            }
        }
    }

    /**
     * LivingEntityがダメージを受けたときに呼び出されるイベントハンドラ。
     * ここでモブからの攻撃力を強化します。
     */
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        // ダメージソースを取得します。
        DamageSource source = event.getSource();

        // ダメージソースの直接の原因（例：矢）と真の原因（例：スケルトン）を取得します。
        // getTrueSource() を使うことで、弓矢や魔法など間接的な攻撃の攻撃主も正しく取得できます。
        if (source.getEntity() instanceof Mob attacker) {

            // 攻撃者がMobであり、かつ攻撃者が自分自身にダメージを与えている場合（例：棘の鎧の反射ダメージ）は除外します。
            if (attacker == event.getEntity()) {
                return;
            }

            // 元のダメージ量を取得します。
            float originalDamage = event.getAmount();

            // ダメージ量を1.5倍にします。
            float newDamage = originalDamage * ModConfig.DAMAGE_MULTIPLIER.get().floatValue();

            // 計算後のダメージ量をイベントにセットします。
            event.setAmount(newDamage);
        }
    }
}