package racoonman.r3d.render.resource;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.codec.PrimitiveCodec;
import racoonman.r3d.resource.io.ClassPathReader;

public class Decoders {
	
	public static IDecoder<IShaderProgram> forProgram(Service service) {
		return ArrayCodec.of(IDecoder.of(
			ShaderStage.NAME_CODEC.fetch("stage"),
			PrimitiveCodec.STRING.fetch("entry"), 
			PrimitiveCodec.STRING.fetch("file"), 
			ArrayCodec.STRING.fetchOr("args", () -> new String[0]),
			(stage, entry, file, args) -> {
				return service.createShader(stage, entry, file, ClassPathReader.readContents(file), args);
			}	
		), IShader[]::new).map(service::createProgram);
	}
}
