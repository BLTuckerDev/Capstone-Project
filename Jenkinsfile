node{

    stage 'Checkout'

    checkout scm

    sh "./gradlew clean"

    stage 'Test'

    sh "./gradlew test"

    sh "gcloud auth activate-service-account --key-file ${env.GCLOUD_AUTH_FILE}"
    sh "gcloud beta test android run --project ${env.GCLOUD_NANODEGREE_CAPSTONE_PROJECT_ID} --app app/build/outputs/apk/app-debug-unaligned.apk  --test app/build/outputs/apk/app-debug-androidTest-unaligned.apk"

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew assembleDebug"

    stage 'Stage Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: 'app/build/test-results/**/TEST-*.xml'])

}