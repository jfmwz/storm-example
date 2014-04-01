java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 \
-Ddruid.realtime.specFile=realtime.spec -classpath druid-services-0.5.39-SNAPSHOT-selfcontained.jar:druid-indexing-hadoop-0.5.39-SNAPSHOT.jar \
com.metamx.druid.indexer.HadoopDruidIndexerMain batchConfig.json


