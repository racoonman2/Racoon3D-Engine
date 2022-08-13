package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST_WITH_ADJACENCY;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_LINE_STRIP;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_LINE_STRIP_WITH_ADJACENCY;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_PATCH_LIST;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_POINT_LIST;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST_WITH_ADJACENCY;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP;
import static org.lwjgl.vulkan.VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP_WITH_ADJACENCY;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum Topology {
	POINT_LIST(VK_PRIMITIVE_TOPOLOGY_POINT_LIST),
	LINE_LIST(VK_PRIMITIVE_TOPOLOGY_LINE_LIST),
	LINE_STRIP(VK_PRIMITIVE_TOPOLOGY_LINE_STRIP),
	TRIANGLE_LIST(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST),
	TRIANGLE_STRIP(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP),
	TRIANGLE_FAN(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN),
	LINE_LIST_WITH_ADJACENCY(VK_PRIMITIVE_TOPOLOGY_LINE_LIST_WITH_ADJACENCY),
	LINE_STRIP_WITH_ADJACENCY(VK_PRIMITIVE_TOPOLOGY_LINE_STRIP_WITH_ADJACENCY),
	TRIANGLE_LIST_WITH_ADJACENCY(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST_WITH_ADJACENCY),
	TRIANGLE_STRIP_WITH_ADJACENCY(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP_WITH_ADJACENCY),
	PATCH_LIST(VK_PRIMITIVE_TOPOLOGY_PATCH_LIST);

	public static final ICodec<Topology> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<Topology> NAME_CODEC = EnumCodec.byName(Topology::valueOf);
	
	private int vkTopology;
	
	private Topology(int vkTopology) {
		this.vkTopology = vkTopology;
	}
	
	public int getVkTopology() {
		return this.vkTopology;
	}
	
	public static Topology lookup(int vkTopology) {
		for(Topology topology : Topology.values()) {
			if(topology.getVkTopology() == vkTopology) {
				return topology;
			}
		}
		
		throw new IllegalArgumentException("Unknown topology [" + vkTopology + "]");
	}
}
