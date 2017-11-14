#!/usr/bin/env groovy

node {
    def current=sh 'pwd'

    def date = sh returnStdout: true, script: 'date "+%m%d"'

// name 
    def program_name="micro_blog"
    // def lib1_name="micro_proto"

// default branch
    def program_default_branch="master"
    // def lib1_default_branch="master"

// git addr 
    // def lib1_git_addr='http://wps.ktkt.com/app2017/micro_proto.git'
    def program_git_addr='http://wps.ktkt.com/app2017/blog.git'

    println 'current dir : ' + current

// stk_thrift
//    stage('检出'+lib1_name) { // for display purposes
//         def lib1_branch=input(
//             id: lib1_name + '_id', message: '输入' + lib1_name + '分支', ok: '确定', parameters: [string(defaultValue: lib1_default_branch, description: lib1_name + ' branch name', name: lib1_name + '_branch')]
//         )

//        checkout([$class: 'GitSCM', branches: [[name: '*/' + lib1_branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'lib']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '3c150308-80b3-45ac-9a94-9c574a087775', url: lib1_git_addr]]])

//      echo  lib1_name + '_branch : ' + lib1_branch
//    }

   stage('检出'+program_name) {
       def program_branch=input(
            id: program_name + '_id', message: '输入' + program_name + '分支', ok: '确定', parameters: [string(defaultValue: program_default_branch, description: program_name + ' branch name', name: program_name + '_branch')]
        )
        
        def m=checkout([$class: 'GitSCM', branches: [[name: '*/' + program_branch]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'program']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '3c150308-80b3-45ac-9a94-9c574a087775', url: program_git_addr]]])

        echo 'checkout ' + program_name + ' : ' + program_branch
   }

   stage('构建') {

       sh 'ls'
       sh """#!/usr/bin/env bash
       
        echo "build program now.current dir:"
        cd program
        pwd

        ./build.sh + ${program_name}
        """
        echo 'build ...'
   }

   stage('传输'){
       echo 'scp ...'

       sh '/root/user_bin/publish_micro.sh ' + program_name
   }

   echo env.JENKINS_HOME // /root/.jenkins/
   echo env.JOB_NAME  // gofront

    stage('完成'){
       deleteDir()

       deleteDir()
    }
}
