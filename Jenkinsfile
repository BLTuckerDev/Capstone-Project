node{

    stage 'Checkout'

    checkout scm

    sh "./gradlew clean"

    stage 'Test'

    sh "./gradlew test"

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew assembleDebug"

    stage 'Stage Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/*.apk', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: 'app/build/test-results/**/TEST-*.xml'])
    archive 'app/build/reports/tests/**/*.html, app/build/reports/tests/**/*.css, app/build/reports/tests/**/*.js'

}