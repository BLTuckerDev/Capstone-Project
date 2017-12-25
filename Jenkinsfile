node{

    stage 'Checkout'

    checkout scm

    sh "./gradlew clean"

    stage 'Setup workspace'

    sh "cp /opt/google-services.json app/"

    stage 'Unit Test'

    sh "./gradlew test"

    stage "Sonar Analysis"

    sh "./gradlew sonarqube"

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew assembleDebug"
    sh "./gradlew assembleDebugAndroidTest"

    stage 'Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/**/*.apk', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: 'app/build/test-results/**/TEST-*.xml'])
     publishHTML (target: [
          allowMissing: false,
          alwaysLinkToLastBuild: false,
          keepAll: true,
          reportDir: 'app/build/reports/coverage/debug',
          reportFiles: 'index.html',
          reportName: "Local Unit Test Coverage"
        ])

    stage 'Cloud Test Lab'

    sh "gcloud auth activate-service-account --key-file ${env.GCLOUD_AUTH_FILE}"

    sh "gcloud beta test android run --project ${env.GCLOUD_NANODEGREE_CAPSTONE_PROJECT_ID} --app app/build/outputs/apk/debug/app-debug.apk --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk --device-ids Nexus5,Nexus6,Nexus9 --os-version-ids 21,22,23 --locales en --orientations portrait,landscape"

}