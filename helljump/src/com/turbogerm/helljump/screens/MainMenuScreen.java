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
package com.turbogerm.helljump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.turbogerm.germlibrary.controls.CustomButtonAction;
import com.turbogerm.germlibrary.controls.CustomImageButton;
import com.turbogerm.helljump.HellJump;
import com.turbogerm.helljump.resources.ResourceNames;
import com.turbogerm.helljump.screens.general.LibGdxLogo;
import com.turbogerm.helljump.screens.general.ScreenBackground;
import com.turbogerm.helljump.screens.general.TurboGermLogo;

public final class MainMenuScreen extends ScreenBase {
    
    private static final float TITLE_X = 30.0f;
    private static final float TITLE_Y = 719.0f;
    
    private static final float BUTTON_X = 105.0f;
    private static final float START_BUTTON_Y = 500.0f;
    private static final float HIGH_SCORE_BUTTON_Y = 400.0f;
    private static final float CREDITS_BUTTON_Y = 300.0f;
    
    private final ScreenBackground mScreenBackground;
    
    private final Sprite mTitleSprite;
    private final TurboGermLogo mTurboGermLogo;
    private final LibGdxLogo mLibGdxLogo;
    
    public MainMenuScreen(HellJump game) {
        super(game);
        
        mGuiStage.addListener(getStageInputListener());
        
        mScreenBackground = new ScreenBackground(mCameraData, mAssetManager);
        
        TextureAtlas atlas = mAssetManager.get(ResourceNames.GRAPHICS_GUI_ATLAS);
        
        mTitleSprite = atlas.createSprite(ResourceNames.GUI_MAIN_MENU_TITLE_IMAGE_NAME);
        mTitleSprite.setPosition(TITLE_X, TITLE_Y);
        
        mTurboGermLogo = new TurboGermLogo(mAssetManager);
        mLibGdxLogo = new LibGdxLogo(mAssetManager);
        
        
        // menu buttons
        CustomImageButton startButton = new CustomImageButton(
                BUTTON_X, START_BUTTON_Y,
                atlas,
                ResourceNames.GUI_MAIN_MENU_BUTTON_START_UP_IMAGE_NAME,
                ResourceNames.GUI_MAIN_MENU_BUTTON_START_DOWN_IMAGE_NAME,
                getStartAction(),
                mAssetManager);
        startButton.addToStage(mGuiStage);
        
        CustomImageButton highScoreButton = new CustomImageButton(
                BUTTON_X, HIGH_SCORE_BUTTON_Y,
                atlas,
                ResourceNames.GUI_MAIN_MENU_BUTTON_HIGH_SCORE_UP_IMAGE_NAME,
                ResourceNames.GUI_MAIN_MENU_BUTTON_HIGH_SCORE_DOWN_IMAGE_NAME,
                getHighScoreAction(),
                mAssetManager);
        highScoreButton.addToStage(mGuiStage);
        
        CustomImageButton creditsButton = new CustomImageButton(
                BUTTON_X, CREDITS_BUTTON_Y,
                atlas,
                ResourceNames.GUI_MAIN_MENU_BUTTON_CREDITS_UP_IMAGE_NAME,
                ResourceNames.GUI_MAIN_MENU_BUTTON_CREDITS_DOWN_IMAGE_NAME,
                getCreditsAction(),
                mAssetManager);
        creditsButton.addToStage(mGuiStage);
    }
    
    @Override
    public void show() {
        super.show();
        
        mScreenBackground.reset();
    }
    
    @Override
    protected void updateImpl(float delta) {
        mScreenBackground.update(delta);
    }
    
    @Override
    public void renderImpl() {
        mBatch.begin();
        mScreenBackground.render(mBatch);
        mTitleSprite.draw(mBatch);
        mTurboGermLogo.render(mBatch);
        mLibGdxLogo.render(mBatch);
        mBatch.end();
    }
    
    private InputListener getStageInputListener() {
        return new InputListener() {
            
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                    Gdx.app.exit();
                    return true;
                }
                
                return false;
            }
        };
    }
    
    private CustomButtonAction getStartAction() {
        return new CustomButtonAction() {
            
            @Override
            public void invoke() {
                mGame.setScreen(HellJump.PLAY_SCREEN_NAME);
            }
        };
    }
    
    private CustomButtonAction getHighScoreAction() {
        return new CustomButtonAction() {
            
            @Override
            public void invoke() {
                mGame.setScreen(HellJump.HIGH_SCORE_SCREEN_NAME);
            }
        };
    }
    
    private CustomButtonAction getCreditsAction() {
        return new CustomButtonAction() {
            
            @Override
            public void invoke() {
                mGame.setScreen(HellJump.CREDITS_SCREEN_NAME);
            }
        };
    }
}