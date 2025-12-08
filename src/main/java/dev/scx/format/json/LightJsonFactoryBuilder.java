package dev.scx.format.json;

import tools.jackson.core.base.DecorableTSFactory.DecorableTSFBuilder;
import tools.jackson.core.json.JsonFactoryBuilder;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.RecyclerPool;

/// 轻量级的 JsonFactory 创建器.
///
/// @author scx567888
/// @version 0.0.1
/// @see JsonFactoryBuilder
final class LightJsonFactoryBuilder extends DecorableTSFBuilder<LightJsonFactory, LightJsonFactoryBuilder> {

    final JsonNodeConvertOptions _jacksonConfig;
    final ByteQuadsCanonicalizer _byteSymbolCanonicalizer;

    public LightJsonFactoryBuilder(JsonNodeConvertOptions jacksonConfig, ByteQuadsCanonicalizer byteSymbolCanonicalizer, RecyclerPool<BufferRecycler> recyclerPool) {
        super(jacksonConfig.toStreamReadConstraints(),
            jacksonConfig.toStreamWriteConstraints(),
            jacksonConfig.errorReportConfiguration(),
            jacksonConfig.toFormatReadFeatures(),
            jacksonConfig.toFormatWriteFeatures());
        this._jacksonConfig = jacksonConfig;
        this._byteSymbolCanonicalizer = byteSymbolCanonicalizer;
        this._recyclerPool = recyclerPool;
        this._factoryFeatures = jacksonConfig.toFactoryFeatures();
        this._streamReadFeatures = jacksonConfig.toStreamReadFeatures();
        this._streamWriteFeatures = jacksonConfig.toStreamWriteFeature();
    }

    @Override
    public LightJsonFactory build() {
        return new LightJsonFactory(this);
    }

}
