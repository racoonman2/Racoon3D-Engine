package racoonman.r3d.render.api.vulkan;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import racoonman.r3d.render.api.vulkan.PipelineLayout.PushConstantRange;

class ComputeCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComputeCache.class);
	
	private Map<ComputeState, ComputePipeline> cache;
	private Device device;
	
	public ComputeCache(Device device) {
		this.cache = new TreeMap<>(ComputeState.COMPARATOR);
		this.device = device;
	}
	
	public ComputePipeline getPipeline(ComputeState state) {
		return this.cache.computeIfAbsent(state, (k) -> {
			LOGGER.info("Auto generated compute pipeline with state [{}]", state);
			return new ComputePipeline(new PipelineLayout(this.device, new DescriptorSetLayout[0], new PushConstantRange[0]), state.shader(), this.device);
		});
	}
}
