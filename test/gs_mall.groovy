#!/usr/bin/env groovy

// 1.输入执行文件名称
// 2.选择服务器ip
// 3.选择发布的名称api,server, admin
// 4.执行发布程序

package view.run_input_required

node {

    def program_name="gs_mall"
    /////////////// 输入发布版本号 ////////////////////
    stage '输入发布版本号'
    // Define an input step and capture the outcome from it.
    def version = input id: 'Version_id', message: '输入版本号', ok: 'OKay', parameters: [string(defaultValue: '', description: 'something like : 201709251734_5578cc0', name: 'version_name')]

    /////////////// 选择服务器 ////////////////////
    stage '选择服务器'
    // Define an input step and capture the outcome from it.
    def ip_list = input id: 'ip_list_id',
          message: '选择服务器',
          ok: 'Okay',
          parameters: [
            [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: false,
            name: 'all'
          ],
           [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: false,
            name: '10.9.189.69'
          ],
          [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: false,
            name: '10.9.123.245',
          ],
          [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: true,
            name: '10.9.187.9',
          ],
          [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: false,
            name: '10.9.98.204',
          ],
          [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: false,
            name: '10.9.173.127',
          ],
          [ 
            $class: 'BooleanParameterDefinition',
            defaultValue: true,
            name: '10.9.197.16',
          ]
    ]

    /////////////// 选择发布项目 ////////////////////

    stage '输入服务'
    def service = input id: 'service_id', message: '选择服务：all, server, api, admin', ok: 'OKay', parameters: [string(defaultValue: 'all', description: '', name: 'service_name')]
//    def service_out = input id: 'Run-test-suites',
//           message: 'Workflow Configuration',
//           ok: 'Okay',
//           parameters: [
//           [
//             $class: 'ChoiceParameterDefinition', choices: 'all \nserver \napi \nadmin', 
//             name: 'select',
//             description: 'A select box option'
//           ],
//           [ 
//             $class: 'BooleanParameterDefinition',
//             defaultValue: true,
//             name: '',
//             description: ''
//           ],

//     ]     
    
    // def service = "${service_out.get('select')}"
    // def  service="all"

    /////////////// 获取参数 ////////////////////

    stage '发布'
    //  发布一个脚本到远程服务器，执行该脚本
    // Echo the outcome values so they can be checked fro in the test. This will help
    // verify that input submit/proceed worked properly.
    def ip_list_1= "${ip_list.get('10.9.189.69')}"
    def ip_list_2= "${ip_list.get('10.9.123.245')}"
    def ip_list_3= "${ip_list.get('10.9.187.9')}"
    def ip_list_4= "${ip_list.get('10.9.98.204')}"
    def ip_list_5= "${ip_list.get('10.9.173.127')}"
    def ip_list_6= "${ip_list.get('10.9.197.16')}"
    def ip_all= "${ip_list.get('all')}"

    def list = []

 if ("$ip_list_1" == "true" || "$ip_all" == "true") {
        list.add("10.9.189.69")
    } 

    if("$ip_list_2" == "true" || "$ip_all" == "true" ){
        list.add("10.9.123.245")
    }
     if("$ip_list_3" == "true" || "$ip_all" == "true" ){
        list.add("10.9.187.9")
    }
     if("$ip_list_4" == "true" || "$ip_all" == "true" ){
        list.add("10.9.98.204")
    }
     if("$ip_list_5" == "true" || "$ip_all" == "true" ){
        list.add("10.9.173.127")
    }

    if ("$ip_list_6" == "true" || "$ip_all" == "true") {
        list.add("10.9.197.16")
    } 

    echo "####### params #######"   

    echo "1. program_name: $program_name"
    echo "2. list: $list"
    echo "3. version: $version"
    echo "4. service: $service"

        for (ip in list) {
            println "======================================== server ip : $ip ========================================"

            filesStr=sh(returnStdout: true, script: "ls /apps/deploy/$program_name | grep $version")
            files=filesStr.tokenize()
            println "files : $files"
            for (file in files) {
                println "########============= file name : $file ====================="
                if (file.contains(service) || service == "all"){
                    def clean_name=file.substring(0, file.indexOf('-linux-amd64'))
                    println "start publish this file : clean name :: $clean_name"
                    sshagent(['micro_manager']) {

                        // copy shell
                        sh "scp /apps/deploy/tool/remote_init.sh root@$ip:/data/micro/tool/"
                        sh "scp /apps/deploy/tool/remote_publish.sh root@$ip:/data/micro/tool/"

                        // init program dir
                        echo "1. ssh root@$ip:/data/micro/tool/remote_init.sh $program_name"
                        sh "ssh root@$ip /data/micro/tool/remote_init.sh $program_name"
                        
                        // copy go file
                        echo "2. scp /apps/deploy/$program_name/$file root@$ip:/data/micro/$program_name/"
                        sh "scp /apps/deploy/$program_name/$file root@$ip:/data/micro/$program_name/"

                        // publish go file 
                        echo "3. publish go file "
                        sh "ssh root@$ip /data/micro/tool/remote_publish.sh $program_name $file $clean_name"
                    }
                }else{
                    println "ignore this file."
                }
            }
        }
}

