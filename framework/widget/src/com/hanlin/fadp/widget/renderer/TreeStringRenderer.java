
package com.hanlin.fadp.widget.renderer;

import java.io.IOException;
import java.util.Map;

import com.hanlin.fadp.widget.model.ModelTree;

/**
 * Widget Library - Tree String Renderer interface
 */
public interface TreeStringRenderer {

    public void renderNodeBegin(Appendable writer, Map<String, Object> context, ModelTree.ModelNode node, int depth) throws IOException;
    public void renderNodeEnd(Appendable writer, Map<String, Object> context, ModelTree.ModelNode node) throws IOException;
    public void renderLabel(Appendable writer, Map<String, Object> context, ModelTree.ModelNode.Label label) throws IOException;
    public void renderLink(Appendable writer, Map<String, Object> context, ModelTree.ModelNode.Link link) throws IOException;
    public void renderImage(Appendable writer, Map<String, Object> context, ModelTree.ModelNode.Image image) throws IOException;
    public void renderLastElement(Appendable writer, Map<String, Object> context, ModelTree.ModelNode node) throws IOException;
    public ScreenStringRenderer getScreenStringRenderer(Map<String, Object> context);
}
