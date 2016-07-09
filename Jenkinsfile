node{

    stage 'Checkout'

    checkout scm

    stage 'Build'

    echo "Building branch: ${env.BRANCH_NAME}"

    sh "./gradlew clean assembleDebug"

    stage 'Stage Archive'
    step([$class: 'ArtifactArchiver', artifacts: 'App/build/outputs/apk/*.apk', fingerprint: true])

}