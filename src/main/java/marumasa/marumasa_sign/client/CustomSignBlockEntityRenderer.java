package marumasa.marumasa_sign.client;

import marumasa.marumasa_sign.MarumaSign;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.IOException;
import java.net.URL;

public class CustomSignBlockEntityRenderer extends SignBlockEntityRenderer {

    private final TextureManager textureManager;
    private final MinecraftClient client;

    public CustomSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);

        client = MinecraftClient.getInstance();

        textureManager = client.getTextureManager();

        try {
            textureManager.registerTexture(
                    new Identifier(MarumaSign.MOD_ID, "test"),
                    new NativeImageBackedTexture(NativeImage.read(new URL("http://localhost/test.png").openStream()))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // renderメソッドをオーバーライドして レンダリング処理を変更する
    @Override
    public void render(SignBlockEntity sign, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (true) {

            final ClientPlayerEntity clientPlayer = client.player;

            // もし プレイヤーがスペクテイターモードだったら
            // 通常のMinecraft看板 も レンダリングするようにする
            if (clientPlayer != null && clientPlayer.isSpectator()) {
                // 親クラスのrenderメソッドを呼び出して 看板のレンダリング処理をする
                super.render(sign, tickDelta, matrices, vertexConsumers, light, overlay);
            }

            // 看板URLから画像をレンダリングする

            // getEntityCutout で 透過と半透明 対応の RenderLayer 生成
            final RenderLayer renderLayer = RenderLayer.getEntityTranslucent(new Identifier(MarumaSign.MOD_ID, "test"));

            render(renderLayer, matrices, vertexConsumers, light, overlay);

        } else {
            // 通常のMinecraft看板をレンダリングする

            // 親クラスのrenderメソッドを呼び出して 看板のレンダリング処理をする
            super.render(sign, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }

    public static void render(RenderLayer renderLayer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        matrices.push();

        ReadSignText signText = new ReadSignText();

        final ReadSignText.Translation translation = signText.translation;
        final ReadSignText.Scale scale = signText.scale;

        matrices.translate(translation.x, translation.y, translation.z); // ブロックの中心に移動する
        matrices.scale(scale.x, scale.y, scale.z); // ブロックの半分の大きさにする
        matrices.multiply(signText.rotation); // ブロックの回転

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, -1.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f, -1.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f, 1.0F, -1.0F, 1.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f, 1.0F, -1.0F, -1.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        matrices.pop();
    }


    @Override
    // ここで ブロックエンティティの表示範囲を設定できる
    public int getRenderDistance() {
        return 256;
    }
}
