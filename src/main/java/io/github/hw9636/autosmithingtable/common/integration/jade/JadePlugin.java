package io.github.hw9636.autosmithingtable.common.integration.jade;

import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlock;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new AutoSmithingDataProvider(), AutoSmithingTableBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new AutoSmithingComponentProvider(), AutoSmithingTableBlock.class);
    }
}
