import matplotlib.pyplot as plt
import numpy as np

def create_custom_plot(data_dict, save_path=None):
    """
    创建自定义图表
    
    Args:
        data_dict: 包含各条线数据的字典
        save_path: 保存图片的路径（可选）
    """
    # 设置图表样式
    plt.style.use('default')
    fig, ax = plt.subplots(figsize=(8, 6))
    
    # 定义线条样式
    styles = {
        'LAT': {'color': 'black', 'marker': 's', 'label': 'LAT'},
        'SC': {'color': 'red', 'marker': 'o', 'label': 'SC'},
        'AT*': {'color': 'blue', 'marker': '^', 'label': 'AT*'},
        'LAT*': {'color': 'green', 'marker': 'v', 'label': 'LAT*'}
    }
    
    # 绘制每条线
    for name, style in styles.items():
        data = data_dict[name]
        ax.plot(data['x'], data['y'],
                color=style['color'],
                marker=style['marker'],
                label=style['label'],
                linewidth=1,
                markersize=6,
                linestyle='-',
                markerfacecolor=style['color'],
                markeredgecolor=style['color'])
    
    # 设置坐标轴范围
    ax.set_xlim(700, 2100)
    ax.set_ylim(1.5, 5.5)
    
    # 设置坐标轴标签
    ax.set_xlabel(r'$\theta$', fontsize=12)
    ax.set_ylabel('Time (s)', fontsize=12)
    
    # 设置刻度朝向内部
    ax.tick_params(direction='in', length=6, width=1, top=True, right=True)
    
    # 设置网格
    ax.grid(True, linestyle='--', alpha=0.3)
    
    # 加粗坐标轴线条
    for spine in ax.spines.values():
        spine.set_linewidth(1.5)
    
    # 添加图例
    ax.legend(loc='center left', bbox_to_anchor=(0.01, 0.5))
    
    # 调整布局
    plt.tight_layout()
    
    # 保存图片
    if save_path:
        plt.savefig(save_path, dpi=300, bbox_inches='tight')
    
    return fig

# 示例数据
example_data = {
    'LAT': {
        'x': [800, 1000, 1200, 1400, 1600, 1800, 2000],
        'y': [3.7, 3.8, 3.7, 3.8, 3.85, 3.8, 3.8]
    },
    'SC': {
        'x': [800, 1000, 1200, 1400, 1600, 1800, 2000],
        'y': [4.5, 4.2, 4.7, 5.0, 4.5, 4.6, 4.3]
    },
    'AT*': {
        'x': [800, 1000, 1200, 1400, 1600, 1800, 2000],
        'y': [1.9, 2.0, 2.0, 2.2, 2.2, 2.3, 2.3]
    },
    'LAT*': {
        'x': [800, 1000, 1200, 1400, 1600, 1800, 2000],
        'y': [1.9, 2.1, 2.2, 2.3, 2.5, 2.7, 2.9]
    }
}

# 创建图表并保存
fig = create_custom_plot(example_data, 'custom_plot.png')
plt.show()