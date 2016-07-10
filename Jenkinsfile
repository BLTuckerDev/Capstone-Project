node{

    stage 'Checkout'

    checkout scm

    sh "./gradlew clean"

    stage 'Unit Test'

    sh "./gradlew test"

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew assembleDebug"
    sh "./gradlew assembleDebugAndroidTest"

    stage 'Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: 'app/build/test-results/**/TEST-*.xml'])

    stage 'Cloud Test Lab'

    sh "gcloud auth activate-service-account --key-file ${env.GCLOUD_AUTH_FILE}"

    sh "gcloud beta test android run --project ${env.GCLOUD_NANODEGREE_CAPSTONE_PROJECT_ID} --app app/build/outputs/apk/app-debug-unaligned.apk --test app/build/outputs/apk/app-debug-androidTest-unaligned.apk --device-ids Nexus4,Nexus5,Nexus6,Nexus9 --os-version-ids 21,22,23 --locales en --orientations portrait,landscape"

}