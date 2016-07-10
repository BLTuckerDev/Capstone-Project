node{

    stage 'Checkout'

    checkout scm

    sh "./gradlew clean"

    stage 'Test'

    sh "./gradlew test"

    sh "gcloud beta test android run --app app/build/outputs/apk/app-debug-unaligned.apk  --test app/build/outputs/apk/app-debug-androidTest-unaligned.apk"

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew assembleDebug"

    stage 'Stage Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: 'app/build/test-results/**/TEST-*.xml'])

}