import subprocess
import pandas as pd
import re
import os

# 设置JAR文件路径和命令行参数
jar_path = './out/artifacts/TCQ_LCQ_jar/TCQ_LCQ.jar'

# 数据集、theta、crackBound和算法索引的选项
dataSetIndices = [0, 1, 2]
thetas = [0, 1, 2, 3, 4, 5, 6]
crackBounds = [0, 1, 2, 3, 4, 5, 6]
algoIndices = list(range(9))

# 初始化CSV文件路径
output_csv_path = 'output_file_lcq_tcq_crackbound.csv'

# 初始化CSV文件
if not os.path.exists(output_csv_path):
    df = pd.DataFrame(columns=["dataSet", "theta", "crackBound", "algo_index", "time"])
    df.to_csv(output_csv_path, index=False)

def append_to_csv(data):
    try:
        df = pd.DataFrame(data, columns=["dataSet", "theta", "crackBound", "algo_index", "time"])
        df.to_csv(output_csv_path, mode='a', header=False, index=False)
    except Exception as e:
        print(f"Error appending to CSV: {e}")

# 正则表达式模式，仅匹配最后一行
pattern = re.compile(r"dataSet: (.+?) theta: (.+?) crackBound: (.+?) algo_index: (.+?) time (.+?)$")

# 运行实验
for dataSetIndex in dataSetIndices:
    # 内层循环1：theta选择0-6，crackBound取3，algo_index取6和7
    for algo_index in algoIndices:
        for crackBound in crackBounds:
            command = ['java', '-jar', jar_path, str(dataSetIndex), '3', str(crackBound), str(algo_index)]
            print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
            result = subprocess.run(command, capture_output=True, text=True)
            if result.returncode != 0:
                print(f"Error running command: {result.stderr}")
                continue  # 跳过错误的命令
            output = result.stdout.strip()
            print(f"Output: {output}")  # 打印输出以供调试
            match = pattern.search(output.splitlines()[-1])
            if match:
                dataSetName, q_theta, crackBound, algo_index, time = match.groups()
                data = [[dataSetName, q_theta, crackBound, algo_index, time]]
                append_to_csv(data)  # 追加到CSV
            else:
                print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行


    # 内层循环2：crackBound选择0-6，theta取3，algo_index取6和7
    # for algo_index in [6, 7]:
    # 	for crackBound in crackBounds:
    #         command = ['java', '-jar', jar_path, str(dataSetIndex), '3', str(crackBound), str(algo_index)]
    #         print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
    #         result = subprocess.run(command, capture_output=True, text=True)
    #         if result.returncode != 0:
    #             print(f"Error running command: {result.stderr}")
    #             continue  # 跳过错误的命令
    #         output = result.stdout.strip()
    #         print(f"Output: {output}")  # 打印输出以供调试
    #         match = pattern.search(output.splitlines()[-1])
    #         if match:
    #             dataSetName, q_theta, crackBound, algo_index, time = match.groups()
    #             data = [[dataSetName, q_theta, crackBound, algo_index, time]]
    #             append_to_csv(data)  # 追加到CSV
    #         else:
    #             print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

    # 内层循环3：algo_index选择0-7，theta取3，crackBound取3
    # for algo_index in [8]:
    #     command = ['java', '-jar', jar_path, str(dataSetIndex), '3', '3', str(algo_index)]
    #     print(f"Running command: {' '.join(command)}")  # 打印命令以供调试
    #     result = subprocess.run(command, capture_output=True, text=True)
    #     if result.returncode != 0:
    #         print(f"Error running command: {result.stderr}")
    #         continue  # 跳过错误的命令
    #     output = result.stdout.strip()
    #     print(f"Output: {output}")  # 打印输出以供调试
    #     match = pattern.search(output.splitlines()[-1])
    #     if match:
    #         dataSetName, q_theta, crackBound, algo_index, time = match.groups()
    #         data = [[dataSetName, q_theta, crackBound, algo_index, time]]
    #         append_to_csv(data)  # 追加到CSV
    #     else:
    #         print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

print(f'JAR文件输出已实时写入: {output_csv_path}')
