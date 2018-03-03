package net.thegaminghuskymc.sandboxgame.util;

import net.thegaminghuskymc.sandboxgame.entity.EntityLivingBase;
import net.thegaminghuskymc.sandboxgame.util.text.ITextComponent;

import javax.annotation.Nullable;

public class CombatEntry
{
    private final DamageSource damageSrc;
    private final int time;
    private final float damage;
    private final float health;
    private final String fallSuffix;
    private final float fallDistance;

    public CombatEntry(DamageSource damageSrcIn, int timeIn, float healthAmount, float damageAmount, String fallSuffixIn, float fallDistanceIn)
    {
        this.damageSrc = damageSrcIn;
        this.time = timeIn;
        this.damage = damageAmount;
        this.health = healthAmount;
        this.fallSuffix = fallSuffixIn;
        this.fallDistance = fallDistanceIn;
    }

    /**
     * Get the DamageSource of the CombatEntry instance.
     */
    public DamageSource getDamageSrc()
    {
        return this.damageSrc;
    }

    public float getDamage()
    {
        return this.damage;
    }

    /**
     */
    public boolean isLivingDamageSrc()
    {
        return this.damageSrc.getTrueSource() instanceof EntityLivingBase;
    }

    @Nullable
    public String getFallSuffix()
    {
        return this.fallSuffix;
    }

    @Nullable
    public ITextComponent getDamageSrcDisplayName()
    {
        return this.getDamageSrc().getTrueSource() == null ? null : this.getDamageSrc().getTrueSource().getDisplayName();
    }

    public float getDamageAmount()
    {
        return this.damageSrc == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
    }
}