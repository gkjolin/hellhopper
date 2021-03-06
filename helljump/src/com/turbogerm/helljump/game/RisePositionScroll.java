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
package com.turbogerm.helljump.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.turbogerm.helljump.CameraData;
import com.turbogerm.helljump.HellJump;
import com.turbogerm.helljump.resources.ResourceNames;

public final class RisePositionScroll {
    
    private static final float POSITION_SCROLL_LINE_WIDTH = 5.0f;
    private static final float POSITION_SCROLL_LINE_HEIGHT = HellJump.VIEWPORT_HEIGHT - 60.0f;
    private static final float POSITION_SCROLL_LINE_X = HellJump.VIEWPORT_WIDTH - POSITION_SCROLL_LINE_WIDTH - 5.0f;
    private static final float POSITION_SCROLL_LINE_Y = 10.0f;
    private static final float MIN_POSITION_SCROLL_BOX_SIZE = 5.0f;
    private static final float POSITION_SCROLL_END_LINE_HEIGHT = 4.0f;
    
    private final Sprite mPositionScrollLineSprite;
    private final Sprite mPositionScrollLineAboveSprite;
    private final Sprite mPositionScrollBoxSprite;
    private final Sprite mPositionScrollEndLineSprite;
    
    private final Rectangle mCameraRect;
    
    private float mRiseHeight;
    private float mEffectivePositionScrollLineHeight;
    
    public RisePositionScroll(CameraData cameraData, AssetManager assetManager) {
        
        mCameraRect = cameraData.getGuiCameraRect();
        
        TextureAtlas atlas = assetManager.get(ResourceNames.GRAPHICS_GUI_ATLAS);
        
        mPositionScrollLineSprite = atlas.createSprite(ResourceNames.GUI_PLAY_POSITION_SCROLL_LINE_IMAGE_NAME);
        mPositionScrollLineAboveSprite = atlas.createSprite(ResourceNames.GUI_PLAY_POSITION_SCROLL_LINE_ABOVE_IMAGE_NAME);
        mPositionScrollBoxSprite = atlas.createSprite(ResourceNames.GUI_PLAY_POSITION_SCROLL_BOX_IMAGE_NAME);
        mPositionScrollEndLineSprite = atlas.createSprite(ResourceNames.GUI_PLAY_POSITION_SCROLL_END_LINE_IMAGE_NAME);
    }
    
    public void setRiseHeight(float riseHeight) {
        mRiseHeight = riseHeight;
        mEffectivePositionScrollLineHeight = (mRiseHeight / (mRiseHeight + GameArea.GAME_AREA_HEIGHT)) *
                POSITION_SCROLL_LINE_HEIGHT;
        
        mPositionScrollLineSprite.setBounds(
                POSITION_SCROLL_LINE_X, POSITION_SCROLL_LINE_Y,
                POSITION_SCROLL_LINE_WIDTH, mEffectivePositionScrollLineHeight);
        
        mPositionScrollLineAboveSprite.setBounds(
                POSITION_SCROLL_LINE_X, POSITION_SCROLL_LINE_Y + mEffectivePositionScrollLineHeight,
                POSITION_SCROLL_LINE_WIDTH, POSITION_SCROLL_LINE_HEIGHT - mEffectivePositionScrollLineHeight);
        
        float positionScrollBoxHeight = Math.max(
                GameArea.GAME_AREA_HEIGHT / mRiseHeight * mEffectivePositionScrollLineHeight,
                MIN_POSITION_SCROLL_BOX_SIZE);
        mPositionScrollBoxSprite.setBounds(
                POSITION_SCROLL_LINE_X, 0.0f,
                POSITION_SCROLL_LINE_WIDTH, positionScrollBoxHeight);
        
        float positionScrollEndLineY = POSITION_SCROLL_LINE_Y + mEffectivePositionScrollLineHeight -
                POSITION_SCROLL_END_LINE_HEIGHT;
        mPositionScrollEndLineSprite.setBounds(
                POSITION_SCROLL_LINE_X, positionScrollEndLineY,
                POSITION_SCROLL_LINE_WIDTH, POSITION_SCROLL_END_LINE_HEIGHT);
    }
    
    public void render(SpriteBatch batch, float visibleAreaPosition) {
        
        float scrollLineX = HellJump.VIEWPORT_WIDTH - mCameraRect.x - POSITION_SCROLL_LINE_WIDTH - 5.0f;
        mPositionScrollLineSprite.setX(scrollLineX);
        mPositionScrollLineAboveSprite.setX(scrollLineX);
        mPositionScrollBoxSprite.setX(scrollLineX);
        mPositionScrollEndLineSprite.setX(scrollLineX);
        
        mPositionScrollLineSprite.draw(batch);
        mPositionScrollLineAboveSprite.draw(batch);
        
        float positionScrollBoxY = POSITION_SCROLL_LINE_Y +
                visibleAreaPosition / mRiseHeight * mEffectivePositionScrollLineHeight;
        mPositionScrollBoxSprite.setY(positionScrollBoxY);
        mPositionScrollBoxSprite.draw(batch);
        
        mPositionScrollEndLineSprite.draw(batch);
    }
    
}
