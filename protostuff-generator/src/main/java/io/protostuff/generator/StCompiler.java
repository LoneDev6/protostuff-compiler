package io.protostuff.generator;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.stringtemplate.v4.AttributeRenderer;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import io.protostuff.compiler.model.Enum;
import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Module;
import io.protostuff.compiler.model.Proto;
import io.protostuff.compiler.model.Service;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class StCompiler extends AbstractProtoCompiler {

    public static final String GENERATOR_NAME = "st4";

    public static final String MODULE = "module";
    public static final String MODULE_COMPILER_ENABLED = "module_compiler_enabled";
    public static final String MODULE_COMPILER_TEMPLATE = "module_compiler_template";

    public static final String MODULE_COMPILER_OUTPUT = "module_compiler_output";
    public static final String PROTO = "proto";
    public static final String PROTO_COMPILER_ENABLED = "proto_compiler_enabled";
    public static final String PROTO_COMPILER_TEMPLATE = "proto_compiler_template";

    public static final String PROTO_COMPILER_OUTPUT = "proto_compiler_output";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_COMPILER_ENABLED = "message_compiler_enabled";
    public static final String MESSAGE_COMPILER_TEMPLATE = "message_compiler_template";

    public static final String MESSAGE_COMPILER_OUTPUT = "message_compiler_output";
    public static final String ENUM = "enum";
    public static final String ENUM_COMPILER_ENABLED = "enum_compiler_enabled";
    public static final String ENUM_COMPILER_TEMPLATE = "enum_compiler_template";

    public static final String ENUM_COMPILER_OUTPUT = "enum_compiler_output";
    public static final String SERVICE = "service";
    public static final String SERVICE_COMPILER_ENABLED = "service_compiler_enabled";
    public static final String SERVICE_COMPILER_TEMPLATE = "service_compiler_template";
    public static final String SERVICE_COMPILER_OUTPUT = "service_compiler_output";

    private final STGroup stGroup;

    private final Map<Class<?>, ObjectExtender<?>> extenderMap;

    @Inject
    public StCompiler(OutputStreamFactory outputStreamFactory,
                      @Assisted String templateFileName,
                      @Assisted Map<Class<?>, AttributeRenderer> attributeRendererMap,
                      @Assisted Map<Class<?>, ObjectExtender<?>> extenderMap) {
        super(outputStreamFactory);
        this.extenderMap = extenderMap;
        STGroup group = new STGroupFile(templateFileName);
        for (java.util.Map.Entry<Class<?>, AttributeRenderer> entry : attributeRendererMap.entrySet()) {
            group.registerRenderer(entry.getKey(), entry.getValue());
        }
        group.setListener(new StErrorListener());
        for (Map.Entry<Class<?>, ObjectExtender<?>> entry : extenderMap.entrySet()) {
            Class<?> objectClass = entry.getKey();
            ObjectExtender<Object> extender = (ObjectExtender<Object>) entry.getValue();
            group.registerModelAdaptor(objectClass, new ObjectModelAdaptor() {
                @Override
                public synchronized Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName) throws STNoSuchPropertyException {
                    if (extender.hasProperty(propertyName)) {
                        return extender.getProperty(o, propertyName);
                    }
                    return super.getProperty(interp, self, o, property, propertyName);
                }
            });
        }

        this.stGroup = group;
    }

    @Override
    protected void compile(Module module, Writer writer) {
        compile(MODULE_COMPILER_TEMPLATE, MODULE, module, writer);
    }

    @Override
    protected void compile(Proto proto, Writer writer) {
        compile(PROTO_COMPILER_TEMPLATE, PROTO, proto, writer);
    }

    @Override
    protected void compile(Message message, Writer writer) {
        compile(MESSAGE_COMPILER_TEMPLATE, MESSAGE, message, writer);
    }

    @Override
    protected void compile(io.protostuff.compiler.model.Enum anEnum, Writer writer) {
        compile(ENUM_COMPILER_TEMPLATE, ENUM, anEnum, writer);
    }

    @Override
    protected void compile(Service service, Writer writer) {
        compile(SERVICE_COMPILER_TEMPLATE, SERVICE, service, writer);
    }

    protected void compile(String templateName, String templateArgName, Object templateArgValue, Writer writer) {
        ST st = stGroup.getInstanceOf(templateName);
        if (st == null) {
            throw new GeneratorException("Template %s is not defined", templateName);
        }
        st.add(templateArgName, templateArgValue);
        String result = st.render();
        try {
            writer.append(result);
        } catch (IOException e) {
            throw new GeneratorException("Can not write file: %s", e.getMessage());
        }
    }

    @Override
    protected boolean canProcess(Module module) {
        return getBoolean(MODULE_COMPILER_ENABLED, MODULE, module);
    }

    @Override
    protected boolean canProcess(Proto proto) {
        return getBoolean(PROTO_COMPILER_ENABLED, PROTO, proto);
    }

    @Override
    protected boolean canProcess(Message message) {
        return getBoolean(MESSAGE_COMPILER_ENABLED, MESSAGE, message);
    }

    @Override
    protected boolean canProcess(Enum anEnum) {
        return getBoolean(ENUM_COMPILER_ENABLED, ENUM, anEnum);
    }

    @Override
    protected boolean canProcess(Service service) {
        return getBoolean(SERVICE_COMPILER_ENABLED, SERVICE, service);
    }

    @Override
    protected String getOutputFileName(String basedir, Module module) {
        String filename = getString(MODULE_COMPILER_OUTPUT, MODULE, module);
        return appendBasedir(basedir, filename);
    }

    @Override
    protected String getOutputFileName(String basedir, Proto proto) {
        String filename = getString(PROTO_COMPILER_OUTPUT, PROTO, proto);
        return appendBasedir(basedir, filename);
    }

    @Override
    protected String getOutputFileName(String basedir, Message message) {
        String filename = getString(MESSAGE_COMPILER_OUTPUT, MESSAGE, message);
        return appendBasedir(basedir, filename);
    }

    @Override
    protected String getOutputFileName(String basedir, Enum anEnum) {
        String filename = getString(ENUM_COMPILER_OUTPUT, ENUM, anEnum);
        return appendBasedir(basedir, filename);
    }

    @Override
    protected String getOutputFileName(String basedir, Service service) {
        String filename = getString(SERVICE_COMPILER_OUTPUT, SERVICE, service);
        return appendBasedir(basedir, filename);
    }

    protected String appendBasedir(String basedir, String relativeFilename) {
        if (basedir.charAt(basedir.length() - 1) == getFolderSeparator()) {
            return basedir + relativeFilename;
        } else {
            return basedir + getFolderSeparator() + relativeFilename;
        }
    }

    protected char getFolderSeparator() {
        return File.separatorChar;
    }

    private boolean getBoolean(String stName, String arg, Object value) {
        String templateOutput = getString(stName, arg, value);
        return Boolean.parseBoolean(templateOutput);
    }

    private String getString(String stName, String arg, Object value) {
        ST st = stGroup.getInstanceOf(stName);
        if (st == null) {
            throw new GeneratorException("Template %s is not defined", stName);
        }
        st.add(arg, value);
        return st.render();
    }

    @Override
    public String getName() {
        return GENERATOR_NAME;
    }

}
