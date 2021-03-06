package doclets;

/**
 * idea and parts of the code are from the book "Living documentation": https://leanpub.com/livingdocumentation
 */

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.sun.javadoc.*;

public class GlossaryDoclet extends Doclet {

    private static PrintWriter writer;

    public static boolean start(RootDoc root) {
        try {
            writer = new PrintWriter("glossary.md");
            writer.println("# " + "Glossary");
            process(root);
            writer.close();
        } catch (FileNotFoundException e) {

        }
        return true;
    }

    public static void process(RootDoc root) {
        final ClassDoc[] classes = root.classes();
        for (ClassDoc clss : classes) {
            if (isBusinessMeaningful(clss)) {
                process(clss);
            }
        }
    }

    protected static boolean isBusinessMeaningful(ProgramElementDoc doc) {
        final AnnotationDesc[] annotations = doc.annotations();
        for (AnnotationDesc annotation : annotations) {
            if (annotation.annotationType().qualifiedTypeName()
                    .contains("GlossaryEntry")){
                return true;
            }
        }
        return false;
    }

    protected static void process(ClassDoc clss) {
        writer.println("");
        writer.println("## " + clss.simpleTypeName());
        writer.println(clss.commentText());
        writer.println("");
        if (clss.isEnum()) {
            Arrays.asList(clss.enumConstants()).forEach(GlossaryDoclet::printEnumConstant);
            writer.println("");
            Arrays.asList(clss.methods(false)).forEach(GlossaryDoclet::printMethod);
        } else {
            Arrays.asList(clss.fields(false)).forEach(GlossaryDoclet::printField);
            Arrays.asList(clss.methods(false)).forEach(GlossaryDoclet::printMethod);
        }
    }

    private static void printMethod(MethodDoc m) {
        if (!m.isPublic() || !hasComment(m)) {
            return;
        }
        final String signature = m.name() + m.flatSignature() + ": "
                + m.returnType().simpleTypeName();
        writer.println("- " + signature + " " + m.commentText());
    }


    private static void printField(FieldDoc m) {
        writer.println("* " + m.name() + ": " + m.commentText());
    }

    private static void printEnumConstant(FieldDoc m) {
        if (!m.isPublic() || !hasComment(m)) {
            return;
        }
        writer.println("- - " + m.commentText());
    }


    private static boolean hasComment(ProgramElementDoc doc) {
        return doc.commentText().trim().length() > 0;
    }
}
