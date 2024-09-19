
var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');

var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

// noinspection JSUnusedGlobalSymbols
function initializeCoreMod() {
    return {
        'renderRecursivelyStart': {
            'target': {
                'type': 'METHOD',
                'class': 'software.bernie.geckolib.renderer.GeoRenderer',
                'methodName': ASM.mapMethod('renderRecursively'), // renderRecursively
                'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V'
            },
            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
                instructions.insert(
					ASM.findFirstMethodCall(
                        methodNode,
                        ASM.MethodType.VIRTUAL,
                        'com/mojang/blaze3d/vertex/PoseStack',
                        ASM.mapMethod('pushPose'), // pushPose
                        '()V'
                    ),
                    ASM.listOf(
						new VarInsnNode(Opcodes.ALOAD, 2), //Loads the arguments at that place, indexed at 1
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            'de/dertoaster/multihitboxlib/ASMHooks',
                            'renderRecursivelyStart',
                            '(ILnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)I',
                            false
                        )
                    )
                );
                return methodNode;
            }
        },
		'renderRecursivelyEnd': {
		            'target': {
		                'type': 'METHOD',
		                'class': 'software.bernie.geckolib.renderer.GeoRenderer',
		                'methodName': ASM.mapMethod('renderRecursively'), // renderRecursively
		                'methodDesc': '(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V'
		            },
		            'transformer': function (/*org.objectweb.asm.tree.MethodNode*/ methodNode) {
		                var /*org.objectweb.asm.tree.InsnList*/ instructions = methodNode.instructions;
		                instructions.insert(
		                    ASM.findFirstMethodCall(
		                        methodNode,
		                        ASM.MethodType.VIRTUAL,
		                        'com/mojang/blaze3d/vertex/PoseStack',
		                        ASM.mapMethod('popPose'), // popPose
		                        '()V'
		                    ),
		                    ASM.listOf(
								// TODO: Reference to oneself
								new VarInsnNode(Opcodes.ALOAD, 0),
								new VarInsnNode(Opcodes.ALOAD, 2), //Loads the arguments at that place, indexed at 1
		                        new MethodInsnNode(
		                            Opcodes.INVOKESTATIC,
		                            'de/dertoaster/multihitboxlib/ASMHooks',
		                            'renderRecursivelyEnd',
		                            '(Lsoftware/bernie/geckolib/renderer/GeoRenderer;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;)V',
		                            false
		                        )
		                    )
		                );
		                return methodNode;
		            }
		        }
    }
}