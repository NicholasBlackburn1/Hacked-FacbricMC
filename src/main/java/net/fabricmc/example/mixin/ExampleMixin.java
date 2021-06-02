package net.fabricmc.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;

import java.nio.ByteBuffer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.View;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashScreen.class)
public class ExampleMixin {

    @Shadow @Final private MinecraftClient client;

    private View view = null;
    private boolean closing = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hookSplashInit(CallbackInfo callbackInfo) {
    }

    @SuppressWarnings("deprecation")
    public void renderMessage(final String message, final float[] colour, int line, float alpha) {
        GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        GL14.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        // STBEasyFont's quads are in reverse order or what OGGL expects, so it gets culled for facing the wrong way.
        // So Disable culling https://github.com/MinecraftForge/MinecraftForge/pull/6824
        RenderSystem.disableCull();
        GL14.glBlendColor(0,0,0, alpha);
        RenderSystem.blendFunc(SrcFactor.CONSTANT_ALPHA, DstFactor.ONE_MINUS_CONSTANT_ALPHA);
        RenderSystem.color3f(colour[0],colour[1],colour[2]);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(10, line * 10, 0);
        RenderSystem.scalef(1, 1, 0);
        RenderSystem.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        RenderSystem.popMatrix();

        RenderSystem.enableCull();
        GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
        MemoryUtil.memFree(charBuffer);
    }


    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hookSplashRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        if (view != null) {
           
                if (this.client.currentScreen != null) {
                    this.client.currentScreen.render(matrices, 0, 0, delta);
                
                float[] color = new float[] { 0.0f, 0.0f, 0.0f};
                color[0] = 16776960/255.0f;

                renderMessage("World Im here Yaa I Hacked Minecraft 1.16.5 UwU",color, ((this.client.currentScreen.height - 15) / 10) -+ 2,  1.0f);

                if (!closing) {
                    closing = true;
                    
                }
            }
            
        }

           

            callbackInfo.cancel();
        }
    }

