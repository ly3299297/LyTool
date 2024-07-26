mvn clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar ^
-Dsonar.projectKey=aoac ^
-Dsonar.java.binaries=target/sonar ^
-Dsonar.host.url=http://172.16.4.241:32090 ^
-Dsonar.host.username=admin ^
-Dsonar.host.password=admin ^
-Dsonar.language=java ^
-Dsonar.sourceEncoding=UTF-8 ^
-Dsonar.issuesReport.console.enable=true ^
-Dsonar.exclusions=**/test/**,**/po/*.java,**/collect/**
