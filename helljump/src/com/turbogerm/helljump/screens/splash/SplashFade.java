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
package com.turbogerm.helljump.screens.splash;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.turbogerm.germlibrary.util.GameUtils;
import com.turbogerm.helljump.CameraData;
import com.turbogerm.helljump.resources.ResourceNames;

public final class SplashFade {
    
    private static final float FADE_IN_DURATION = 1.5f;
    private static final float DELAY_DURATION = 1.0f;
    private static final float FADE_OUT_DURATION = 1.5f;
    
    private static final float DELAY_START = FADE_IN_DURATION;
    private static final float FADE_OUT_START = DELAY_START + DELAY_DURATION;
    private static final float TOTAL_DURATION = FADE_OUT_START + FADE_OUT_DURATION;
    
    private final Sprite mBlackSprite;
    private final Rectangle mCameraRect;
    
    private float mInternalTime;
    
    private boolean mIsFadeOutStarted;
    
    public SplashFade(CameraData cameraData, AssetManager assetManager) {
        
        mCameraRect = cameraData.getGuiCameraRect();
        
        TextureAtlas atlas = assetManager.get(ResourceNames.GRAPHICS_GUI_ATLAS);
        mBlackSprite = atlas.createSprite(ResourceNames.GUI_GENERAL_BLACK_IMAGE_NAME);
    }
    
    public void reset() {
        mInternalTime = 0.0f;
        mIsFadeOutStarted = false;
    }
    
    public void update(float delta) {
        if (mInternalTime < TOTAL_DURATION) {
            if (mInternalTime >= FADE_OUT_START) {
                if (mIsFadeOutStarted) {
                    mInternalTime += delta;
                } else {
                    mInternalTime = FADE_OUT_START;
                }
            } else {
                mInternalTime += delta;
            }
        } else {
            mInternalTime = TOTAL_DURATION;
        }
    }
    
    public void render(SpriteBatch batch) {
        mBlackSprite.setBounds(mCameraRect.x, mCameraRect.y, mCameraRect.width, mCameraRect.height);
        GameUtils.setSpriteAlpha(mBlackSprite, getAlpha());
        mBlackSprite.draw(batch);
    }
    
    private float getAlpha() {
        float alpha;
        if (mInternalTime <= DELAY_START) {
            alpha = (DELAY_START - mInternalTime) / FADE_IN_DURATION;
        } else if (mInternalTime <= FADE_OUT_START) {
            alpha = 0.0f;
        } else {
            alpha = (mInternalTime - FADE_OUT_START) / FADE_OUT_DURATION;
        }
        
        return MathUtils.clamp(alpha, 0.0f, 1.0f);
    }
    
    public boolean isFinished() {
        return mInternalTime >= TOTAL_DURATION;
    }
    
    public boolean isFadeOut() {
        return mIsFadeOutStarted;
    }
    
    public void fadeOut() {
        mIsFadeOutStarted = true;
    }
}
