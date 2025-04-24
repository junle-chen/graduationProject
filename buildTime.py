import subprocess
import pandas as pd
import re
import os

# 设置JAR文件路径和命令行参数
jar_path = './out/artifacts/TCQ_LCQ_jar/TCQ_LCQ.jar'

# 数据集、theta、crackBound和算法索引的选项
dataSetIndices = [0, 1]
thetas = [0, 1, 2, 3, 4, 5, 6]
crackBounds = [3]
algoIndices = [6,7,8]

# 初始化CSV文件路径
output_csv_path = 'output_file_lcq_tcq_buildTime.csv'

# 初始化CSV文件
if not os.path.exists(output_csv_path):
    df = pd.DataFrame(columns=["dataSet", "theta", "crackBound", "algo_index", "buildTime", "time"])
    df.to_csv(output_csv_path, index=False)

def append_to_csv(data):
    try:
        df = pd.DataFrame(data, columns=["dataSet", "theta", "crackBound", "algo_index", "buildTime", "time"])
        df.to_csv(output_csv_path, mode='a', header=False, index=False)
    except Exception as e:
        print(f"Error appending to CSV: {e}")

# 更新正则表达式模式，以匹配包含buildTime的最后一行
pattern = re.compile(r"dataSet: (.+?) theta: (.+?) crackBound: (.+?) algo_index: (.+?) buildTime(.+?) time (.+?)$")

# 运行实验
for dataSetIndex in dataSetIndices:
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
                dataSetName, q_theta, crackBound, algo_index, buildTime, time = match.groups()
                data = [[dataSetName, q_theta, crackBound, algo_index, buildTime.strip(), time.strip()]]
                append_to_csv(data)  # 追加到CSV
            else:
                print(f"Line did not match: {output.splitlines()[-1]}")  # 输出未匹配的行

print(f'JAR文件输出已实时写入: {output_csv_path}')
