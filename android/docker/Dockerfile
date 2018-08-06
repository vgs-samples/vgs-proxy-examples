FROM anapsix/alpine-java:latest
MAINTAINER Gordon Young <gjyoung1974@gmail.com>

# Setup

ENV VERSION_SDK_TOOLS=3952940 ANDROID_HOME=/usr/local/android-sdk-linux
ENV PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

RUN mkdir -p $ANDROID_HOME && chown -R root.root $ANDROID_HOME

# Install dependencies
RUN apk add --no-cache bash curl git openssl openssh-client ca-certificates 

# Install Android SDK
RUN wget -q -O sdk.zip http://dl.google.com/android/repository/sdk-tools-linux-$VERSION_SDK_TOOLS.zip && \
unzip sdk.zip -d $ANDROID_HOME && \
rm -f sdk.zip && \

# Install licenses
mkdir -p $ANDROID_HOME/licenses/ && \
echo "8933bad161af4178b1185d1a37fbf41ea5269c55" > $ANDROID_HOME/licenses/android-sdk-license && \
echo "84831b9409646a918e30573bab4c9c91346d8abd" > $ANDROID_HOME/licenses/android-sdk-preview-license

# Install and update Android packages
ADD packages.txt $ANDROID_HOME

RUN mkdir -p /root/.android && \
    touch /root/.android/repositories.cfg

RUN yes | $ANDROID_HOME/tools/bin/sdkmanager "add-ons;addon-google_apis-google-24" && \
yes | $ANDROID_HOME/tools/bin/sdkmanager "build-tools;26.0.0" &&\
yes | $ANDROID_HOME/tools/bin/sdkmanager "platform-tools" &&\
yes | $ANDROID_HOME/tools/bin/sdkmanager "platforms;android-26" &&\
yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;android;m2repository" &&\
yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;google;google_play_services" &&\
yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;google;m2repository"


# Gradle
ENV GRADLE_VERSION 4.1
ENV GRADLE_SDK_URL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip
RUN curl -sSL "${GRADLE_SDK_URL}" -o gradle-${GRADLE_VERSION}-bin.zip  \
        && unzip gradle-${GRADLE_VERSION}-bin.zip -d ${ANDROID_HOME}  \
        && rm -rf gradle-${GRADLE_VERSION}-bin.zip
ENV GRADLE_HOME ${ANDROID_HOME}/gradle-${GRADLE_VERSION}
ENV PATH ${GRADLE_HOME}/bin:$PATH

