/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2013 Goran Mrzljak
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.turbogerm.helljump.game.character.states;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turbogerm.germlibrary.util.GameUtils;
import com.turbogerm.germlibrary.util.Pools;
import com.turbogerm.helljump.dataaccess.EnemyData;
import com.turbogerm.helljump.game.CollisionEffects;
import com.turbogerm.helljump.game.GameArea;
import com.turbogerm.helljump.game.PlatformToCharCollisionData;
import com.turbogerm.helljump.game.RiseSection;
import com.turbogerm.helljump.game.character.CharacterEffects;
import com.turbogerm.helljump.game.character.GameCharacter;
import com.turbogerm.helljump.game.character.graphics.CharacterBodyGraphics;
import com.turbogerm.helljump.game.character.graphics.CharacterEyesGraphicsFart;
import com.turbogerm.helljump.game.character.graphics.CharacterEyesGraphicsNormal;
import com.turbogerm.helljump.game.character.graphics.CharacterHeadGraphics;
import com.turbogerm.helljump.game.character.graphics.FartDischargeGraphics;
import com.turbogerm.helljump.game.character.graphics.ShieldEffectGraphics;
import com.turbogerm.helljump.game.enemies.EnemyBase;
import com.turbogerm.helljump.game.items.ItemBase;
import com.turbogerm.helljump.game.platforms.PlatformBase;
import com.turbogerm.helljump.resources.ResourceNames;

final class NormalCharacterState extends CharacterStateBase {
    
    private static final int SIGNET_SCORE_INCREMENT = 100;
    private static final int FALL_DEATH_FARTS = 1;
    private static final float DEATH_SHIELD_DURATION = 3.0f;
    
    private static final float HIGH_JUMP_POWER_MULTIPLIER = 1.3f;
    private static final float HIGH_JUMP_SPEED = GameCharacter.JUMP_SPEED * HIGH_JUMP_POWER_MULTIPLIER;
    
    private static final float FART_POWER_MULTIPLIER = 1.3f;
    private static final float FART_JUMP_SPEED = GameCharacter.JUMP_SPEED * FART_POWER_MULTIPLIER;
    
    private static final Color JUMP_SUIT_COLOR;
    
    private final CharacterBodyGraphics mCharacterBodyGraphics;
    private final CharacterHeadGraphics mCharacterHeadGraphics;
    private final CharacterEyesGraphicsNormal mCharacterEyesGraphicsNormal;
    private final CharacterEyesGraphicsFart mCharacterEyesGraphicsFart;
    private final ShieldEffectGraphics mShieldEffectGraphics;
    private final FartDischargeGraphics mFartDischargeGraphics;
    
    private boolean mIsDying;
    
    private final Rectangle mRect;
    
    private final CharCollisionData mCharCollisionData;
    private final CollisionEffects mCollisionEffects;
    
    private final CharacterStateChangeData mCharacterStateChangeData;
    
    private final Sound mJumpSound;
    private final Sound mJumpBoostSound;
    private final Sound mCoinSound;
    private final Sound mItemSound;
    private final Sound mFartSound;
    
    static {
        JUMP_SUIT_COLOR = Color.RED;
    }
    
    public NormalCharacterState(CharacterStateManager characterStateManager, AssetManager assetManager) {
        super(characterStateManager);
        
        mCharacterBodyGraphics = new CharacterBodyGraphics(assetManager);
        mCharacterHeadGraphics = new CharacterHeadGraphics(assetManager);
        mCharacterEyesGraphicsNormal = new CharacterEyesGraphicsNormal(assetManager);
        mCharacterEyesGraphicsFart = new CharacterEyesGraphicsFart(assetManager);
        mShieldEffectGraphics = new ShieldEffectGraphics(assetManager);
        mFartDischargeGraphics = new FartDischargeGraphics(assetManager);
        
        mRect = new Rectangle(0.0f, 0.0f, GameCharacter.WIDTH, GameCharacter.HEIGHT);
        
        mCharCollisionData = new CharCollisionData();
        mCollisionEffects = new CollisionEffects();
        
        mCharacterStateChangeData = new CharacterStateChangeData();
        
        mJumpSound = assetManager.get(ResourceNames.SOUND_JUMP);
        mJumpBoostSound = assetManager.get(ResourceNames.SOUND_JUMP_BOOST);
        mCoinSound = assetManager.get(ResourceNames.SOUND_COIN);
        mItemSound = assetManager.get(ResourceNames.SOUND_ITEM);
        mFartSound = assetManager.get(ResourceNames.SOUND_FART);
    }
    
    @Override
    public void reset() {
        mCharacterBodyGraphics.reset();
        mCharacterHeadGraphics.reset();
        mCharacterEyesGraphicsNormal.reset();
        mCharacterEyesGraphicsFart.reset();
        mShieldEffectGraphics.reset();
        mFartDischargeGraphics.reset();
        
        mIsDying = false;
    }
    
    @Override
    public void update(CharacterStateUpdateData updateData) {
        
        Vector2 position = updateData.characterPosition;
        Vector2 speed = updateData.characterSpeed;
        float visibleAreaPosition = updateData.visibleAreaPosition;
        float horizontalSpeed = updateData.horizontalSpeed;
        CharacterEffects characterEffects = updateData.characterEffects;
        float delta = updateData.delta;
        
        handleFall(position, speed, visibleAreaPosition, characterEffects);
        
        if (!mIsDying) {
            if (!characterEffects.isFarting()) {
                handleCollisionWithPlatform(
                        position,
                        speed,
                        visibleAreaPosition,
                        updateData.platformToCharCollisionData,
                        updateData.activeRiseSections,
                        updateData.visiblePlatforms,
                        characterEffects,
                        delta);
                
                speed.x = horizontalSpeed;
                position.x = GameUtils.getPositiveModulus(
                        position.x + GameCharacter.CHARACTER_CENTER_X_OFFSET, GameArea.GAME_AREA_WIDTH) -
                        GameCharacter.CHARACTER_CENTER_X_OFFSET;
            } else {
                if (speed.y <= 0.0f) {
                    speed.y = FART_JUMP_SPEED;
                    characterEffects.subtractFart();
                    mFartDischargeGraphics.discharge();
                    mCharacterEyesGraphicsFart.closeEyes();
                    
                    applyRiseSectionEffects(updateData.activeRiseSections, false, true);
                    
                    mFartSound.play();
                }
                updatePositionAndSpeed(position, speed, horizontalSpeed, delta);
            }
        } else {
            updatePositionAndSpeed(position, speed, horizontalSpeed, delta);
        }
        
        mRect.x = position.x;
        mRect.y = position.y;
        
        if (!mIsDying) {
            handleCollisionWithEnemies(position, updateData.visibleEnemies, characterEffects);
        }
        
        if (!mIsDying) {
            handleCollisionWithItems(position, updateData.visibleItems, characterEffects);
        }
        
        if (!mIsDying && position.y > updateData.riseHeight) {
            changeState(CharacterStateManager.END_CHARACTER_STATE);
        }
        
        characterEffects.update(delta);
        
        if (characterEffects.isHighJump()) {
            mCharacterBodyGraphics.setColor(JUMP_SUIT_COLOR);
        } else {
            mCharacterBodyGraphics.setColor(CharacterBodyGraphics.DEFAULT_COLOR);
        }
        
        if (!characterEffects.isFarting()) {
            mCharacterEyesGraphicsNormal.update(delta);
        } else {
            mCharacterEyesGraphicsFart.update(delta);
        }
        
        mShieldEffectGraphics.update(delta);
        mShieldEffectGraphics.setShieldEffectRemaining(characterEffects.getShieldRemaining());
        mFartDischargeGraphics.update(delta);
    }
    
    @Override
    public void render(CharacterStateRenderData renderData) {
        SpriteBatch batch = renderData.batch;
        Vector2 position = renderData.characterPosition;
        CharacterEffects characterEffects = renderData.characterEffects;
        
        mFartDischargeGraphics.render(batch, position);
        mCharacterBodyGraphics.render(batch, position);
        mCharacterHeadGraphics.render(batch, position);
        if (characterEffects.isFarting()) {
            mCharacterEyesGraphicsFart.render(batch, position);
        } else {
            mCharacterEyesGraphicsNormal.render(batch, position);
        }
        mShieldEffectGraphics.render(batch, position);
    }
    
    private void handleFall(Vector2 position, Vector2 speed, float visibleAreaPosition,
            CharacterEffects characterEffects) {
        
        if (position.y <= 0.0f) {
            position.y = 0.0f;
            speed.y = getJumpSpeed(characterEffects);
        } else if (position.y < visibleAreaPosition) {
            if (characterEffects.getLives() <= 0) {
                mIsDying = true;
                changeState(CharacterStateManager.DYING_FALL_CHARACTER_STATE);
            } else {
                position.y = visibleAreaPosition + GameUtils.EPSILON;
                characterEffects.subtractLife();
                characterEffects.setFarts(FALL_DEATH_FARTS);
                characterEffects.setShield(DEATH_SHIELD_DURATION);
            }
        }
    }
    
    private void handleCollisionWithPlatform(
            Vector2 position,
            Vector2 speed,
            float visibleAreaPosition,
            PlatformToCharCollisionData platformToCharCollisionData,
            Array<RiseSection> activeRiseSections,
            Array<PlatformBase> visiblePlatforms,
            CharacterEffects characterEffects,
            float delta) {
        
        if (!platformToCharCollisionData.isCollision) {
            Vector2 cpNext = Pools.obtainVector();
            cpNext.set(position.x + speed.x * delta, position.y + speed.y * delta);
            Vector2 intersection = Pools.obtainVector();
            
            if (isCollisionWithPlatform(
                    visiblePlatforms, position, cpNext, intersection, mCharCollisionData)) {
                position.set(intersection);
                
                handleFall(position, speed, visibleAreaPosition, characterEffects);
                if (!mIsDying) {
                    handleCollisionWithPlatform(position, speed, activeRiseSections, characterEffects);
                }
            } else {
                position.set(cpNext);
                handleFall(position, speed, visibleAreaPosition, characterEffects);
                speed.y = Math.max(speed.y - GameCharacter.GRAVITY * delta, -GameCharacter.JUMP_SPEED);
            }
            
            Pools.freeVector(cpNext);
            Pools.freeVector(intersection);
        } else {
            position.y = platformToCharCollisionData.collisionPoint.y;
            mCharCollisionData.collisionPlatform = platformToCharCollisionData.collisionPlatform;
            mCharCollisionData.collisionPointX = platformToCharCollisionData.collisionPoint.x;
            
            handleFall(position, speed, visibleAreaPosition, characterEffects);
            if (!mIsDying) {
                handleCollisionWithPlatform(position, speed, activeRiseSections, characterEffects);
            }
        }
    }
    
    private void handleCollisionWithPlatform(
            Vector2 position,
            Vector2 speed,
            Array<RiseSection> activeRiseSections,
            CharacterEffects characterEffects) {
        
        mCharCollisionData.collisionPlatform.getCollisionEffects(
                mCharCollisionData.collisionPointX, mCollisionEffects);
        
        if (mCollisionEffects.isEffectActive(CollisionEffects.BURN) && !characterEffects.isShielded()) {
            if (characterEffects.getLives() <= 0) {
                mIsDying = true;
                mCollisionEffects.clear();
                changeState(CharacterStateManager.DYING_FIRE_CHARACTER_STATE);
                return;
            } else {
                characterEffects.subtractLife();
                characterEffects.setShield(DEATH_SHIELD_DURATION);
            }
        }
        
        if (mCollisionEffects.isEffectActive(CollisionEffects.JUMP_BOOST)) {
            speed.y = mCollisionEffects.getValue(CollisionEffects.JUMP_BOOST,
                    CollisionEffects.JUMP_BOOST_SPEED_INDEX);
            float volume = mCollisionEffects.getValue(CollisionEffects.JUMP_BOOST,
                    CollisionEffects.JUMP_BOOST_SOUND_VOLUME_INDEX);
            mJumpBoostSound.play(volume);
        } else {
            speed.y = getJumpSpeed(characterEffects);
            mJumpSound.play();
        }
        
        applyRiseSectionEffects(activeRiseSections,
                mCollisionEffects.isEffectActive(CollisionEffects.REPOSITION_PLATFORMS),
                mCollisionEffects.isEffectActive(CollisionEffects.VISIBLE_ON_JUMP));
        
        mCollisionEffects.clear();
    }
    
    private void applyRiseSectionEffects(Array<RiseSection> activeRiseSections,
            boolean isReposition, boolean isVisibleOnJump) {
        
        if (isReposition) {
            for (RiseSection riseSection : activeRiseSections) {
                riseSection.applyEffect(CollisionEffects.REPOSITION_PLATFORMS);
            }
        }
        
        if (isVisibleOnJump) {
            for (RiseSection riseSection : activeRiseSections) {
                riseSection.applyEffect(CollisionEffects.VISIBLE_ON_JUMP);
            }
        }
    }
    
    private void handleCollisionWithEnemies(Vector2 position, Array<EnemyBase> visibleEnemies,
            CharacterEffects characterEffects) {
        
        if (characterEffects.isShielded()) {
            return;
        }
        
        for (EnemyBase enemy : visibleEnemies) {
            if (enemy.isCollision(mRect)) {
                if (characterEffects.getLives() <= 0) {
                    mIsDying = true;
                    mCharacterStateChangeData.clear();
                    mCharacterStateChangeData.setData(
                            CharacterStateChangeData.IS_SAW_KEY, EnemyData.SAW_TYPE.equals(enemy.getType()));
                    changeState(CharacterStateManager.DYING_ENEMY_CHARACTER_STATE, mCharacterStateChangeData);
                } else {
                    characterEffects.subtractLife();
                    characterEffects.setShield(DEATH_SHIELD_DURATION);
                }
                return;
            }
        }
    }
    
    private void handleCollisionWithItems(Vector2 position, Array<ItemBase> visibleItems,
            CharacterEffects characterEffects) {
        for (ItemBase item : visibleItems) {
            if (item.isExisting() && item.isCollision(mRect)) {
                handleItemPickUp(item, characterEffects);
                item.pickUp();
                return;
            }
        }
    }
    
    private void handleItemPickUp(ItemBase item, CharacterEffects characterEffects) {
        int effect = item.getEffect();
        
        int score = 0;
        switch (effect) {
            case ItemBase.FART_EFFECT:
                characterEffects.setFarts((Integer) item.getValue());
                mItemSound.play();
                break;
            
            case ItemBase.SHIELD_EFFECT:
                characterEffects.setShield((Float) item.getValue());
                mItemSound.play();
                break;
            
            case ItemBase.HIGH_JUMP_EFFECT:
                characterEffects.setHighJump((Float) item.getValue());
                mItemSound.play();
                break;
            
            case ItemBase.EXTRA_LIFE_EFFECT:
                characterEffects.addLife();
                mItemSound.play();
                break;
            
            case ItemBase.SCORE_EFFECT:
                characterEffects.addScore((Integer) item.getValue());
                mCoinSound.play();
                break;
            
            case ItemBase.SIGNET_EFFECT:
                characterEffects.addSignet();
                score = SIGNET_SCORE_INCREMENT * characterEffects.getNumSignets();
                characterEffects.addScore(score);
                item.setPickedUpText(String.format("+%d PTS", score));
                mCoinSound.play();
                break;
            
            default:
                break;
        }
    }
    
    private static boolean isCollisionWithPlatform(
            Array<PlatformBase> platforms,
            Vector2 c1, Vector2 c2, Vector2 intersection, CharCollisionData charCollisionData) {
        
        // only check for collision when character is going down
        if (c2.y >= c1.y) {
            return false;
        }
        
        for (PlatformBase platform : platforms) {
            if (platform.isCollision(c1, c2, intersection)) {
                charCollisionData.collisionPlatform = platform;
                charCollisionData.collisionPointX = intersection.x;
                return true;
            }
        }
        
        return false;
    }
    
    private static float getJumpSpeed(CharacterEffects characterEffects) {
        return characterEffects.isHighJump() ? HIGH_JUMP_SPEED : GameCharacter.JUMP_SPEED;
    }
    
    private static class CharCollisionData {
        public PlatformBase collisionPlatform;
        public float collisionPointX;
    }
}
