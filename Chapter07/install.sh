mvn install:install-file \
  -DgroupId=fixparser \
  -DartifactId=fixparser \
  -Dpackaging=jar \
  -Dversion=0.7.3 \
  -Dfile=lib/fixparser-0.7.3.jar \
  -DgeneratePom=true

mvn install:install-file \
  -DgroupId=druid \
  -DartifactId=druid-services \
  -Dpackaging=jar \
  -Dversion=0.4.6-SNAPSHOT \
  -Dfile=lib/druid-services-0.4.6-SNAPSHOT.jar \
  -DgeneratePom=true
