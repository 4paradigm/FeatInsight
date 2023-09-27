<template>

<div class="content">
  <div class="app-stencil" ref="stencilContainer">
  </div>
  <div class="app-content" ref="container">
  </div>
</div>
  <a-button type="primary" @click="getData()">保存DAG</a-button>
  <a-button type="success" @click="runGraph()">运行任务</a-button>
<div>
  <right-drawer v-if="showRight" @updateVisable="updateVisableFn" :node-data="filterFn(nodeData)" :select-cell="selectCell"></right-drawer>
</div>
<div>
  <right-drawer-res v-if="showRes" :out_SQL="out_SQL"></right-drawer-res>
</div>
</template>

<script>
import axios from 'axios'
import { Graph, Shape, FunctionExt } from '@antv/x6'
import { Stencil } from '@antv/x6-plugin-stencil'
import { Transform } from '@antv/x6-plugin-transform'
import { Selection } from '@antv/x6-plugin-selection'
import { Snapline } from '@antv/x6-plugin-snapline'
import { Keyboard } from '@antv/x6-plugin-keyboard'
import { Clipboard } from '@antv/x6-plugin-clipboard'
import { History } from '@antv/x6-plugin-history'
import insertCss from 'insert-css'
import RightDrawer from './DAG/RightDrawer.vue'
import RightDrawerRes from './DAG/RightDrawerRes.vue'
import TaskNode from './DAG/TaskNode.vue'
import '@antv/x6-vue-shape'
import { message } from 'ant-design-vue';

export default {
  components: {
    RightDrawer,
    RightDrawerRes,
  },

  data() {
    return {
      searchText: "",

      data: {
        // 节点
        nodes: [
          {
            id: 'node1', // String，可选，节点的唯一标识
            x: 40,       // Number，必选，节点位置的 x 值
            y: 40,       // Number，必选，节点位置的 y 值
            width: 80,   // Number，可选，节点大小的 width 值
            height: 40,  // Number，可选，节点大小的 height 值
            label: 'hello', // String，节点标签
          },
          {
            id: 'node2', // String，节点的唯一标识
            x: 160,      // Number，必选，节点位置的 x 值
            y: 180,      // Number，必选，节点位置的 y 值
            width: 80,   // Number，可选，节点大小的 width 值
            height: 40,  // Number，可选，节点大小的 height 值
            label: 'world', // String，节点标签
          },
        ],
        // 边
        edges: [
          {
            source: 'node1', // String，必须，起始节点 id
            target: 'node2', // String，必须，目标节点 id
          },
        ],
      },
      showRight: false,
      graph: null,
      nodeData:[],
      nodeId: '',
      templateLists: [],
      out_SQL: '',
      showRes: false
    };
  },

  mounted() {
    this.initData();
  },

  methods: {
    initData() {
      const graph = new Graph({
        container: this.$refs.container,
        width: 800,
        height: 600,
        grid: true,
        mousewheel: {
          enabled: true,
          zoomAtMousePosition: true,
          modifiers: 'ctrl',
          minScale: 0.5,
          maxScale: 3,
        },
        connecting: {
          router: 'manhattan',
          connector: {
            name: 'rounded',
            args: {
              radius: 8,
            },
          },
          anchor: 'center',
          connectionPoint: 'anchor',
          allowBlank: false,
          snap: {
            radius: 20,
          },
          createEdge() {
            return new Shape.Edge({
              attrs: {
                line: {
                  stroke: '#A2B1C3',
                  strokeWidth: 2,
                  targetMarker: {
                    name: 'block',
                    width: 12,
                    height: 8,
                  },
                },
              },
              zIndex: 0,
            })
          },
          validateConnection({ targetMagnet }) {
            if (targetMagnet) {
              const group = targetMagnet.getAttribute("port-group");
              if (group) {
                return group === "top";
              }
            }

            return !!targetMagnet;
          },
          validateMagnet({ magnet }) {
            const group = magnet.getAttribute("port-group");
            if (group) {
              return group === "bottom";
            }
            return true;
          }
        },
        highlighting: {
          magnetAdsorbed: {
            name: 'stroke',
            args: {
              attrs: {
                fill: '#5F95FF',
                stroke: '#5F95FF',
              },
            },
          },
        },
      });
      // #endregion

      // #region 使用插件
      graph
        .use(
          new Transform({
            resizing: false,
            rotating: false,
          }),
        )
        .use(
          new Selection({
            rubberband: true,
            showNodeSelectionBox: true,
          }),
        )
        .use(new Snapline())
        .use(new Keyboard())
        .use(new Clipboard())
        .use(new History());
      // #endregion


      // #region 初始化 stencil
      const stencil = new Stencil({
        title: '任务节点',
        target: graph,
        stencilGraphWidth: 200,
        stencilGraphHeight: 180,
        collapsable: true,
        groups: [
          {
            title: '任务节点',
            name: 'group1',
            collapsable: false,
          },
        ],
        layoutOptions: 
          {    
            columns: 1,
            columnWidth: 80,
            rowHeight: 55,
          },
      });

      this.$refs.stencilContainer.appendChild(stencil.container);
      // #endregion


      // #region 快捷键与事件
      graph.bindKey(['meta+c', 'ctrl+c'], () => {
        const cells = graph.getSelectedCells()
        if (cells.length) {
          graph.copy(cells)
        }
        return false
      })
      graph.bindKey(['meta+x', 'ctrl+x'], () => {
        const cells = graph.getSelectedCells()
        if (cells.length) {
          graph.cut(cells)
        }
        return false
      })
      graph.bindKey(['meta+v', 'ctrl+v'], () => {
        if (!graph.isClipboardEmpty()) {
          const cells = graph.paste({ offset: 32 })
          graph.cleanSelection()
          graph.select(cells)
        }
        return false
      })

      // undo redo
      graph.bindKey(['meta+z', 'ctrl+z'], () => {
        if (graph.canUndo()) {
          graph.undo()
        }
        return false
      })
      graph.bindKey(['meta+shift+z', 'ctrl+shift+z'], () => {
        if (graph.canRedo()) {
          graph.redo()
        }
        return false
      })

      // select all
      graph.bindKey(['meta+a', 'ctrl+a'], () => {
        const nodes = graph.getNodes()
        if (nodes) {
          graph.select(nodes)
        }
      })

      // delete
      graph.bindKey('backspace', () => {
        const cells = graph.getSelectedCells()
        if (cells.length) {
          graph.removeCells(cells)
        }
      })

      // zoom
      graph.bindKey(['ctrl+1', 'meta+1'], () => {
        const zoom = graph.zoom()
        if (zoom < 1.5) {
          graph.zoom(0.1)
        }
      });

      graph.bindKey(['ctrl+2', 'meta+2'], () => {
        const zoom = graph.zoom()
        if (zoom > 0.5) {
          graph.zoom(-0.1)
        }
      });

      // #endregion

      // #region 初始化图形
      const ports = {
        groups: {
          top: {
            position: 'top',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
                style: {
                  visibility: 'hidden',
                },
              },
            },
          },
          bottom: {
            position: 'bottom',
            attrs: {
              circle: {
                r: 4,
                magnet: true,
                stroke: '#5F95FF',
                strokeWidth: 1,
                fill: '#fff',
                style: {
                  visibility: 'hidden',
                },
              },
            },
          },
        },
        items: [
          {
            group: 'top',
          },
          {
            group: 'bottom',
          },
        ],
      };

      Graph.registerNode(
        'vue-rect',
        {
          inherit: 'rect',
          width: 66,
          height: 36,
          attrs: {
            body: {
              strokeWidth: 1,
              stroke: '#5F95FF',
              fill: '#EFF4FF',
              rx: 6,
              ry: 6,
            },
            text: {
              fontSize: 12,
              fill: '#262626',
            },
          },
          ports: {
            ...ports,
            items: [
              {
                group: 'top',
              },
              {
                group: 'bottom',
              },
            ],
          },
          component: { TaskNode }
        },
        true,
      );


      const r1 = graph.createNode({
        shape: 'vue-rect',
        data: {
              name: '',
              desc: ''
            },
        label: 'Node',
      });

      stencil.load([r1], 'group1');

      //graph.fromJSON(this.data);
      //graph.centerContent()

      this.graph=graph
      this.graph.on('blank:click', () => {
        this.nodeId = ''
        this.showRight = false
        this.showRes = false
      })

      // 连接线鼠标移入
      this.graph.on('edge:mouseenter', ({ edge }) => {
        edge.addTools([
          'source-arrowhead',
          'target-arrowhead',
          {
            name: 'button-remove',
            args: {
              distance: -30,
            }
          }
        ])
      })
      // 连接线鼠标移出
      this.graph.on('edge:mouseleave', ({ edge }) => {
        edge.removeTools()
      })

      // 节点点击
      this.graph.on('node:click', ({ node }) => {
        const data = node.store.data
        const { id } = data
        this.nodeId = id
        this.showRight = true

      })

      // cell 节点时才触发
      this.graph.on('node:added', ({ node }) => {
        const data = node.store.data
        const obj = {
            id: data.id,
            name: '',
            desc: ''
        }
        this.nodeData.push(obj)

      })

      this.graph.on('node:removed', ({ node }) => {
        const data = node.store.data
        const posIndex = this.nodeData.findIndex((item) => item.id === data.id)
        this.nodeData.splice(posIndex, 1)
      })
      this.graph.on('selection:changed', (args) => {
        args.added.forEach(cell => {
          //console.log(cell)
          this.selectCell = cell
        })
      })

      const containerRef = this.$refs.container
      // 节点鼠标移入
      this.graph.on('node:mouseenter', FunctionExt.debounce(({ node }) => {
          // 添加连接点
          const ports = containerRef.querySelectorAll('.x6-port-body')
          this.showPorts(ports, true)

          // 添加删除
          const type = node.store.data.type
          const x = type === 'taskNode' ? 300 : 60
          node.addTools({
            name: 'button-remove',
            args: {
              x: 0,
              y: 0,
              offset: { x: x, y: 10 },
            },
          })
        }),
        500
      )
      // 节点鼠标移出
      this.graph.on('node:mouseleave', ({ node }) => {
        // 添加连接点
        const ports = containerRef.querySelectorAll('.x6-port-body')
        this.showPorts(ports, false)

        // 移除删除
        node.removeTools()
      })



    },

    // 显示连线节点
    showPorts (ports, show) {
      for (let i = 0, len = ports.length; i < len; i = i + 1) {
        ports[i].style.visibility = show ? 'visible' : 'hidden'
      }
    },

    getData() {
      console.log(this.nodeData)
      console.log(this.graph)
      console.log(this.graph.toJSON())
    },

    updateVisableFn(val) {
      this.nodeData.taskId = ''
      this.showRight = val
    },

    filterFn(data) {
      const nodeId = this.nodeId
      let result = {}
      
      if (nodeId && data.length > 0) {
        const posIndex = data.findIndex(item => item.id === nodeId)
        if (posIndex >= 0) {
          result = data[posIndex]
        }
      }

      return result
    },
    saveFn() {
      const data = this.graph.toJSON()
      const len = this.templateLists.length
      this.templateLists.push({
        name: `模板${len + 1}`,
        data
      })
    },


    runGraph() {
      console.log(this.graph.toJSON()['cells']);
      axios.post(`http://127.0.0.1:5000/sql`, this.graph.toJSON()['cells'])
      .then(response => {
        console.log(response.data);
        message.success(`Conversion Success`);
        this.out_SQL=response.data;
        this.showRes=true;
      })
      .catch(error => {
        if ("response" in error && "data" in error.response) {
          message.error(error.response.data);
        } else {
          message.error(error.message);
        }
      });
    }
  },
};
</script>



<style scoped>
 
.content {
  font-family: sans-serif;
  display: flex;
  border: 1px solid #dfe3e8;
}
 
.app-stencil {
  width: 100px;
  border: 1px solid #dfe3e8;
  position: relative;
}
 
.app-content {
  flex: 1;
  height: 520px;
  margin-left: 8px;
  margin-right: 8px;
  box-shadow: 0 0 10px 1px #e9e9e9;
}
 
.x6-graph-scroller {
  border: 1px solid #f0f0f0;
  margin-left: -1px;
}

#container {
  display: flex;
  border: 1px solid #dfe3e8;
}
#stencil {
  width: 180px;
  height: 100%;
  position: relative;
  border-right: 1px solid #dfe3e8;
}
#graph-container {
  width: calc(100% - 180px);
  height: 100%;
}
.x6-widget-stencil  {
  background-color: #fff;
}
.x6-widget-stencil-title {
  background-color: #fff;
}
.x6-widget-stencil-group-title {
  background-color: #fff !important;
}
.x6-widget-transform {
  margin: -1px 0 0 -1px;
  padding: 0px;
  border: 1px solid #239edd;
}
.x6-widget-transform > div {
  border: 1px solid #239edd;
}
.x6-widget-transform > div:hover {
  background-color: #3dafe4;
}
.x6-widget-transform-active-handle {
  background-color: #3dafe4;
}
.x6-widget-transform-resize {
  border-radius: 0;
}
.x6-widget-selection-inner {
  border: 1px solid #239edd;
}
.x6-widget-selection-box {
  opacity: 0;
}


</style>