package racoonman.r3d.render.vertex;

import java.util.ArrayList;
import java.util.List;

import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.InputRate;
import racoonman.r3d.render.util.DataType;
import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.PrimitiveCodec;

public class VertexFormat {
	public static final VertexFormat EMPTY = new VertexFormat(InputRate.VERTEX);
	public static final ICodec<VertexFormat> CODEC = ICodec.simple(InputRate.ORDINAL_CODEC.fetch("input_rate", VertexFormat::getInputRate), ICodec.list(Attribute.CODEC, Attribute[]::new).fetch("attributes", VertexFormat::getAttributes).map((attributes) -> attributes.toArray(Attribute[]::new)), VertexFormat::new);

	private List<Attribute> attributes;
	private int stride;
	private InputRate inputRate;

	public VertexFormat(InputRate inputRate, Attribute... attributes) {
		this.inputRate = inputRate;
		this.attributes = new ArrayList<>();

		this.attributes(attributes);
	}

	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	public Attribute getAttribute(int index) {
		return this.attributes.get(index);
	}

	public VertexFormat attributes(Attribute... attributes) {
		for (Attribute attribute : attributes) {
			this.attributes.add(attribute);
			this.stride += attribute.size();
		}
		return this;
	}

	public InputRate getInputRate() {
		return this.inputRate;
	}

	public boolean hasAttribute(Attribute attribute) {
		return this.attributes.contains(attribute);
	}

	public int getStride() {
		return this.stride;
	}

	public int getAttributeCount() {
		return this.attributes.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VertexFormat) {
			return this.attributes.equals(((VertexFormat) obj).getAttributes());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "VertexFormat[" + this.attributes.toString() + "]";
	}

	public static record Attribute(String id, DataType dataType, int dimensions, Format format) {
		public static final ICodec<Attribute> CODEC = ICodec.simple(PrimitiveCodec.STRING.fetch("id", Attribute::id), DataType.ORDINAL_CODEC.fetch("data_type", Attribute::dataType), PrimitiveCodec.INT.fetch("dimensions", Attribute::dimensions), Format.ORDINAL_CODEC.fetch("format", Attribute::format), Attribute::new);

		public static final Attribute VEC_1I = new Attribute("1i", DataType.INT32, 1, Format.R32_SINT);
		public static final Attribute VEC_2I = new Attribute("2i", DataType.INT32, 2, Format.R32G32_SINT);
		public static final Attribute VEC_3I = new Attribute("3i", DataType.INT32, 3, Format.R32G32B32_SINT);
		public static final Attribute VEC_4I = new Attribute("4i", DataType.INT32, 4, Format.R32G32B32A32_SINT);

		public static final Attribute VEC_1UI = new Attribute("1ui", DataType.INT32, 1, Format.R32_UINT);
		public static final Attribute VEC_2UI = new Attribute("2ui", DataType.INT32, 2, Format.R32G32_UINT);
		public static final Attribute VEC_3UI = new Attribute("3ui", DataType.INT32, 3, Format.R32G32B32_UINT);
		public static final Attribute VEC_4UI = new Attribute("4ui", DataType.INT32, 4, Format.R32G32B32A32_UINT);

		public static final Attribute VEC_1F = new Attribute("1f", DataType.FLOAT32, 1, Format.R32_SFLOAT);
		public static final Attribute VEC_2F = new Attribute("2f", DataType.FLOAT32, 2, Format.R32G32_SFLOAT);
		public static final Attribute VEC_3F = new Attribute("3f", DataType.FLOAT32, 3, Format.R32G32B32_SFLOAT);
		public static final Attribute VEC_4F = new Attribute("4f", DataType.FLOAT32, 4, Format.R32G32B32A32_SFLOAT);

		public static final Attribute VEC_1S = new Attribute("1s", DataType.INT16, 1, Format.R16_SINT);
		public static final Attribute VEC_2S = new Attribute("2s", DataType.INT16, 2, Format.R16G16_SINT);
		public static final Attribute VEC_3S = new Attribute("3s", DataType.INT16, 3, Format.R16G16B16_SINT);
		public static final Attribute VEC_4S = new Attribute("4s", DataType.INT16, 4, Format.R16G16B16A16_SINT);

		public static final Attribute VEC_1US = new Attribute("1us", DataType.INT16, 1, Format.R16_UINT);
		public static final Attribute VEC_2US = new Attribute("2us", DataType.INT16, 2, Format.R16G16_UINT);
		public static final Attribute VEC_3US = new Attribute("3us", DataType.INT16, 3, Format.R16G16B16_UINT);
		public static final Attribute VEC_4US = new Attribute("4us", DataType.INT16, 4, Format.R16G16B16A16_UINT);

		public static final Attribute VEC_1D = new Attribute("1d", DataType.FLOAT64, 1, Format.R64_SFLOAT);
		public static final Attribute VEC_2D = new Attribute("2d", DataType.FLOAT64, 2, Format.R64G64_SFLOAT);
		public static final Attribute VEC_3D = new Attribute("3d", DataType.FLOAT64, 3, Format.R64G64B64_SFLOAT);
		public static final Attribute VEC_4D = new Attribute("4d", DataType.FLOAT64, 4, Format.R64G64B64A64_SFLOAT);

		public static final Attribute VEC_1B = new Attribute("1b", DataType.BYTE, 1, Format.R8_SINT);
		public static final Attribute VEC_2B = new Attribute("2b", DataType.BYTE, 2, Format.R8G8_SINT);
		public static final Attribute VEC_3B = new Attribute("3b", DataType.BYTE, 3, Format.R8G8B8_SINT);
		public static final Attribute VEC_4B = new Attribute("4b", DataType.BYTE, 4, Format.R8G8B8A8_SINT);

		public static final Attribute VEC_1UB = new Attribute("1ub", DataType.BYTE, 1, Format.R8_UINT);
		public static final Attribute VEC_2UB = new Attribute("2ub", DataType.BYTE, 2, Format.R8G8_UINT);
		public static final Attribute VEC_3UB = new Attribute("3ub", DataType.BYTE, 3, Format.R8G8B8_UINT);
		public static final Attribute VEC_4UB = new Attribute("4ub", DataType.BYTE, 4, Format.R8G8B8A8_UINT);

		public static final Attribute TEX_ID = wrap("tex_id", VEC_1UI);
		public static final Attribute PADDING_1B = wrap("pad_1b", VEC_1B);
		public static final Attribute POSITION_2F = wrap("pos_2f", VEC_2F);
		public static final Attribute POSITION_3F = wrap("pos_3f", VEC_3F);
		public static final Attribute NORMAL_3F = wrap("normal_3f", VEC_3F);
		public static final Attribute COLOR_4F = wrap("color_4f", VEC_4F);
		public static final Attribute COLOR_3F = wrap("color_3f", VEC_3F);
		public static final Attribute TEX_2F = wrap("tex_2f", VEC_2F);
		public static final Attribute TEX_3F = wrap("tex_3f", VEC_3F);
		public static final Attribute TANGENT_3F = wrap("tangent_3f", VEC_3F);
		public static final Attribute BITANGENT_3F = wrap("bitangent_3f", VEC_3F);
		public static final Attribute LIGHT_3F = wrap("light_3f", VEC_3F);

		public int size() {
			return this.dimensions * this.dataType.getSize();
		}

		@Override
		public String toString() {
			return this.id;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Attribute other) {
				return other.id.equals(this.id);
			} else {
				return false;
			}
		}

		public static Attribute wrap(String id, Attribute attribute) {
			return new Attribute(id, attribute.dataType(), attribute.dimensions(), attribute.format());
		}
	}
}