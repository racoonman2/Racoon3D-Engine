package racoonman.r3d.render.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.codec.PrimitiveCodec;

public class Decoders {
	
	public static IDecoder<IShaderProgram> forProgram(String... externalArgs) {
		return ArrayCodec.of(forShader(externalArgs), IShader[]::new).map(Driver::createProgram);
	}
	
	public static IDecoder<IShader> forShader(String... externalArgs) {
		return IDecoder.of(
			ShaderStage.NAME_CODEC.fetch("stage"),
			PrimitiveCodec.STRING.fetch("entry"), 
			PrimitiveCodec.STRING.fetch("file"), 
			ArrayCodec.STRING.fetchOr("args", () -> new String[0]),
			(stage, entry, file, args) -> {
				List<String> argList = new ArrayList<>();
				Collections.addAll(argList, args);
				Collections.addAll(argList, externalArgs);
				return Driver.loadShader(stage, entry, file, argList.toArray(String[]::new));
			}
		);
	}
}
