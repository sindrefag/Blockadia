package net.thegaminghuskymc.sandboxgame.client.renderer;

import net.thegaminghuskymc.sandboxgame.client.renderer.vertex.VertexFormat;
import net.thegaminghuskymc.sandboxgame.client.renderer.vertex.VertexFormatElement;
import net.thegaminghuskymc.sgf.fml.relauncher.Side;
import net.thegaminghuskymc.sgf.fml.relauncher.SideOnly;

import java.nio.ByteBuffer;
import java.util.List;

@SideOnly(Side.CLIENT)
public class WorldVertexBufferUploader {
    public void draw(BufferBuilder bufferBuilderIn) {
        if (bufferBuilderIn.getVertexCount() > 0) {
            VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
            int i = vertexformat.getSize();
            ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();

            for (int j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexformatelement = list.get(j);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                int k = vertexformatelement.getType().getGlConstant();
                int l = vertexformatelement.getIndex();
                bytebuffer.position(vertexformat.getOffset(j));

                // moved to VertexFormatElement.preDraw
                vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
            }

            GlStateManager.glDrawArrays(bufferBuilderIn.getDrawMode(), 0, bufferBuilderIn.getVertexCount());
            int i1 = 0;

            for (int j1 = list.size(); i1 < j1; ++i1) {
                VertexFormatElement vertexformatelement1 = list.get(i1);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
                int k1 = vertexformatelement1.getIndex();

                // moved to VertexFormatElement.postDraw
                vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
            }
        }

        bufferBuilderIn.reset();
    }
}