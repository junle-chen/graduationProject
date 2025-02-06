import subprocess
import pandas as pd
import re
import os

# 设置JAR文件路径和命令行参数
jar_path = './out/artifacts/TCQ_LCQ_jar/TCQ_LCQ.jar'

# 数据集、theta、crackBound和算法索引的选项
dataSetIndices = [0, 1]
thetas = [0, 1, 2, 3, 4, 5, 6]
crackBounds = [0, 1, 2, 3, 4, 5, 6]
algoIndices = list(range(9))
lastTimes = [0, 1, 2, 3, 4, 5, 6]
select = 1

# # 初始化CSV文件路径
# output_csv_path = os.path.join('outputData','output_file_lcq_tcq_crackbound.csv')

# # 初始化CSV文件
# if not os.path.exists(output_csv_path):
#     df = pd.DataFrame(columns=["dataSet", "theta", "crackBound", "algo_index", "lastTime","totalTime", "buildTime","queryTime"])
#     df.to_csv(output_csv_path, index=False)

def initOutputFile(fileName):
    # 初始化CSV文件路径
    output_csv_path = os.path.join('outputData',fileName)
    if os.path.exists(output_csv_path):
        os.remove(output_csv_path)    
    # 初始化CSV文件
    if not os.path.exists(output_csv_path):
        df = pd.DataFrame(columns=["dataSet", "theta", "crackBound", "algo_index", "lastTime","totalTime", "buildTime","queryTime"])
        df.to_csv(output_csv_path, index=False)
    return output_csv_path


def append_to_csv(data,output_csv_path):
    try:
        df = pd.DataFrame(data, columns=["dataSet", "theta", "crackBound", "algo_index", "lastTime","totalTime","buildTime","queryTime"])
        df.to_csv(output_csv_path, mode='a', header=False, index=False)
    except Exception as e:
        print(f"Error appending to CSV: {e}")
        
def initQuryNumsFile(fileName):
    # 初始化CSV文件路径
    output_csv_path = os.path.join('outputData',fileName)
    if os.path.exists(output_csv_path):
        os.remove(output_csv_path)    
    # 初始化CSV文件
    if not os.path.exists(output_csv_path):
        df = pd.DataFrame(columns=["dataSetIndex", "algo_index", "query_nums", "single_time","accumulated_time"])
        df.to_csv(output_csv_path, index=False)
    return output_csv_path

def append_querynum_to_csv(data,output_csv_path):
    try:
        df = pd.DataFrame(data, columns=["dataSetIndex", "algo_index", "query_nums", "single_time","accumulated_time"])
        df.to_csv(output_csv_path, mode='a', header=False, index=False)
    except Exception as e:
        print(f"Error appending to CSV: {e}")
        
        
# 正则表达式模式，仅匹配最后一行
pattern = re.compile(r"dataSet: (.+?) theta: (.+?) crackBound: (.+?) algo_index: (.+?) lastTime: (.+?) totalTime: (.+?)ms buildTime: (.+?)ms queryTime (.+?)ms")
pattern_time = re.compile(r".*?(\d+) Test Time: (\d+)ms.*?Accumulated time:(\d+)m")
# 运行实验
for dataSetIndex in dataSetIndices: 
    #TEST query nums
    query_nums_file = initQuryNumsFile(f'querynums_time_{dataSetIndex}.csv')
    for algo_index in algoIndices:
        command = ['java', '-jar', jar_path, str(dataSetIndex), '3', '3', str(algo_index),'3',str(select)]
        print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
        result = subprocess.run(command, capture_output=True, text=True)
        if result.returncode != 0:
            print(f"Error running command: {result.stderr}")
            continue  # 跳过错误的命令
        output = result.stdout.strip()
        print(f"Output: {output}")  # 打印输出以供调试
        for line in output.splitlines():
            match = pattern_time.match(line)
            if match:
                test_number, test_time, accumulated_time = match.groups()
                data = [[dataSetIndex, algo_index, test_number, test_time, accumulated_time]]
                append_querynum_to_csv(data,query_nums_file)  # 追加到CSV
            else:
                print(f"Ignored line: {line}")  # 输出未匹配的行
    #TEST crackBound
    ck_file = initOutputFile(f'cracking_bound_time_{dataSetIndex}.csv')
    for algo_index in algoIndices:
        for crackBound in crackBounds:
            command = ['java', '-jar', jar_path, str(dataSetIndex), '3', str(crackBound), str(algo_index),'3',str(select)]
            print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
            result = subprocess.run(command, capture_output=True, text=True)
            if result.returncode != 0:
                print(f"Error running command: {result.stderr}")
                continue  # 跳过错误的命令
            output = result.stdout.strip()
            print(f"Output: {output}")  # 打印输出以供调试
            match = pattern.search(output.splitlines()[-1])
            if match:
                dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime = match.groups()
                data = [[dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime]]
                append_to_csv(data,ck_file)  # 追加到CSV
            else:
                print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

    theta_file = initOutputFile(f'theta_time_{dataSetIndex}.csv')
    #TEST theta
    for algo_index in algoIndices:
        for theta in thetas:
            command = ['java', '-jar', jar_path, str(dataSetIndex), str(theta), '3', str(algo_index),'3',str(select)]
            print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
            result = subprocess.run(command, capture_output=True, text=True)
            if result.returncode != 0:
                print(f"Error running command: {result.stderr}")
                continue  # 跳过错误的命令
            output = result.stdout.strip()
            print(f"Output: {output}")  # 打印输出以供调试
            match = pattern.search(output.splitlines()[-1])
            if match:
                dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime = match.groups()
                data = [[dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime]]
                append_to_csv(data,theta_file)  # 追加到CSV
            else:
                print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

    lastTime_file = initOutputFile(f'lastTime_time_{dataSetIndex}.csv')
    
    #TEST lastTime
    for algo_index in algoIndices:
        for lastTime in lastTimes:
            command = ['java', '-jar', jar_path, str(dataSetIndex), str(theta), '3', str(algo_index),str(lastTime),str(select)]
            print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
            result = subprocess.run(command, capture_output=True, text=True)
            if result.returncode != 0:
                print(f"Error running command: {result.stderr}")
                continue  # 跳过错误的命令
            output = result.stdout.strip()
            print(f"Output: {output}")  # 打印输出以供调试
            match = pattern.search(output.splitlines()[-1])
            if match:
                dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime = match.groups()
                data = [[dataSetName, q_theta, crackBound, algo_index, lastTime, testTime, buildTime, queryTime]]
                append_to_csv(data,lastTime_file)  # 追加到CSV
            else:
                print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

print('End')
