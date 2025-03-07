FROM openjdk:17-oracle AS base

# feat insight version
ARG VERSION=0.2.0
ARG TARGETARCH
WORKDIR /app

FROM base AS base_amd64
ONBUILD ADD https://github.com/4paradigm/FeatInsight/releases/download/v${VERSION}/featinsight-${VERSION}.jar /app/

FROM base AS base_arm64
ONBUILD ADD https://github.com/4paradigm/FeatInsight/releases/download/v${VERSION}/featinsight-${VERSION}-linux-aarch64.jar /app/featinsight-${VERSION}.jar

FROM base_${TARGETARCH}

EXPOSE 8888

CMD ["java", "-jar", "/app/featinsight-${VRESION}.jar"]
