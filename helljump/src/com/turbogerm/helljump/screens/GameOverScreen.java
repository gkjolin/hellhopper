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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.turbogerm.germlibrary.controls.CustomButtonAction;
import com.turbogerm.germlibrary.controls.CustomImageButton;
import com.turbogerm.helljump.HellJump;
import com.turbogerm.helljump.resources.ResourceNames;
import com.turbogerm.helljump.screens.general.ScreenBackground;

public final class GameOverScreen extends ScreenBase {
    
    private static final float BUTTON_X = 105.0f;
    private static final float BUTTON_Y = 20.0f;
    
    private static final int NAME_MAX_LENGTH = 20;
    
    private final ScreenBackground mScreenBackground;
    
    private final Label mGameOverLabel;
    private final Label mPlacementLabel;
    private final TextField mNameTextField;
    
    public GameOverScreen(HellJump game) {
        super(game);
        
        mGuiStage.addListener(getStageInputListener(this));
        
        mScreenBackground = new ScreenBackground(mCameraData, mAssetManager);
        
        LabelStyle labelStyle = new LabelStyle(mGuiSkin.get(LabelStyle.class));
        labelStyle.font = mGuiSkin.getFont("xxxl-font");
        
        final float gameOverLabelY = 600.0f;
        final float gameOverLabelHeight = 135.0f;
        
        mGameOverLabel = new Label("", mGuiSkin);
        mGameOverLabel.setBounds(0.0f, gameOverLabelY, HellJump.VIEWPORT_WIDTH, gameOverLabelHeight);
        mGameOverLabel.setStyle(labelStyle);
        mGameOverLabel.setAlignment(Align.center);
        mGuiStage.addActor(mGameOverLabel);
        
        final float placementLabelY = gameOverLabelY - gameOverLabelHeight;
        final float placementLabelHeight = 90.0f;
        
        mPlacementLabel = new Label("", mGuiSkin);
        mPlacementLabel.setBounds(0.0f, placementLabelY, HellJump.VIEWPORT_WIDTH, placementLabelHeight);
        mPlacementLabel.setStyle(labelStyle);
        mPlacementLabel.setAlignment(Align.center);
        mGuiStage.addActor(mPlacementLabel);
        
        final float nameTextFieldY = placementLabelY - placementLabelHeight;
        final float nameTextFieldWidth = 360.0f;
        final float nameTextFieldHeight = 50.0f;
        final float nameTextFieldX = (HellJump.VIEWPORT_WIDTH - nameTextFieldWidth) / 2.0f;
        
        mNameTextField = new TextField("", mGuiSkin);
        mNameTextField.setBounds(nameTextFieldX, nameTextFieldY, nameTextFieldWidth, nameTextFieldHeight);
        mNameTextField.setMaxLength(NAME_MAX_LENGTH);
        mGuiStage.addActor(mNameTextField);
        
        TextureAtlas atlas = mAssetManager.get(ResourceNames.GRAPHICS_GUI_ATLAS);
        
        CustomImageButton button = new CustomImageButton(
                BUTTON_X, BUTTON_Y,
                atlas,
                ResourceNames.GUI_BUTTON_CONTINUE_UP_IMAGE_NAME,
                ResourceNames.GUI_BUTTON_CONTINUE_DOWN_IMAGE_NAME,
                getContinueAction(),
                mAssetManager);
        button.addToStage(mGuiStage);
    }
    
    @Override
    public void show() {
        super.show();
        
        mScreenBackground.reset();
        
        int score = mGameData.getScore();
        
        String gameOverText = String.format("Game over!\nYour score is:\n%d", score);
        mGameOverLabel.setText(gameOverText);
        
        int scorePlace = mGameData.getHighScoresData().getPlaceForScore(score);
        if (scorePlace >= 0) {
            String placementText = String.format("You place %d.\non high score list.", scorePlace + 1);
            mPlacementLabel.setText(placementText);
            mNameTextField.setText(mGameData.getHighScoresData().getLastEnteredName());
            mNameTextField.setVisible(true);
        } else {
            mPlacementLabel.setText("");
            mNameTextField.setVisible(false);
        }
    }
    
    @Override
    protected void updateImpl(float delta) {
        mScreenBackground.update(delta);
    }
    
    @Override
    public void renderImpl() {
        mBatch.begin();
        mScreenBackground.render(mBatch);
        mBatch.end();
    }
    
    private static InputListener getStageInputListener(final GameOverScreen loseGameScreen) {
        
        return new InputListener() {
            
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                    loseGameScreen.mGame.setScreen(HellJump.MAIN_MENU_SCREEN_NAME);
                    return true;
                }
                
                return false;
            }
        };
    }
    
    private CustomButtonAction getContinueAction() {
        return new CustomButtonAction() {
            
            @Override
            public void invoke() {
                boolean isHighScore = mGameData.getHighScoresData().insertHighScore(
                        mNameTextField.getText(), mGameData.getScore());
                
                if (isHighScore) {
                    mGame.setScreen(HellJump.HIGH_SCORE_SCREEN_NAME);
                } else {
                    mGame.setScreen(HellJump.MAIN_MENU_SCREEN_NAME);
                }
            }
        };
    }
}
