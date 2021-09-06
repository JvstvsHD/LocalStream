package de.jvstvshd.localstream.network.handling;


import de.jvstvshd.localstream.network.packets.*;

/**
 *
 */
public interface PacketHandler {


    /**
     * A basic implementation of {@link PacketClientHandler} and {@link PacketServerHandler} to avoid NPEs, to give more informative errors and may also to set a correct handler.
     * If one <code>handle...</code> method is called, a <i>real</i> handler is searched and may get set if no errors occur.
     * <h2>DO NOT USE THIS AT RUNTIME! REALLY NOT</h2>
     */
    final class BasicImpl implements PacketClientHandler, PacketServerHandler {

        static {
            try {
                Packet.defaultHandler = create();
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        private static BasicImpl create() throws IllegalAccessException {
            if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != BasicImpl.class)
                throw new IllegalAccessException("cannot create basic implementation instance outside of this class");
            return new BasicImpl(BasicImpl.class);
        }

        private BasicImpl(Class<?> callerClass) throws IllegalAccessException {
            Class<?> realCallerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
            if (realCallerClass != callerClass)
                throw new RuntimeException("Class " + realCallerClass + " tried to create an instance of the packet handler basic implementation as class " + callerClass);
            if (realCallerClass != BasicImpl.class)
                throw new IllegalAccessException("cannot create basic implementation instance outside of this class");
        }

        @SuppressWarnings("ConstantConditions")
        private void handleMethodUse(String handleMethod, Packet<?> packet) {
            throw new UnsupportedOperationException("Operation unsupported.");
            /*System.err.println("This is ONLY and ONLY the BASIC implementation of the PacketHandler and should NOT be used at runtime since it does not really do something.");
            System.err.println("As of this, the packetHandler Packet#defaultHandler will be changed to a handler found while looking in all classed in the package 'de.jvstvshd'");
            List<Throwable> throwables = Lists.newArrayList();
            try {
                Reflections reflections = new Reflections("de.jvstvshd");
                Set<Class<? extends PacketHandler>> classes = reflections.getSubTypesOf(PacketHandler.class);
                System.out.println("Found " + classes.size() + " packet handler implementations, using first one.");
                boolean worked = false;
                for (Class<? extends PacketHandler> aClass : classes) {
                    System.out.println("Trying to set " + aClass.getName() + " as new defaultHandler as this basic impl does not do anything beside changing the default handler and throwing errors.");
                    try {
                        Packet.setDefaultHandler(aClass.getDeclaredConstructor().newInstance());
                        System.out.println("Set " + aClass.getName() + " as new defaultHandler.");
                        worked = true;
                        break;
                    } catch (ReflectiveOperationException e) {
                        throwables.add(e);
                    }
                }
                if (!worked || Packet.defaultHandler == null) {
                    throwables.add(new UnsupportedOperationException("Operation is not supported in this basic implementation"));
                }
                if (Packet.defaultHandler != null) {
                    try {
                        Method method = Packet.defaultHandler.getClass().getDeclaredMethod(handleMethod, packet.getClass());
                        method.invoke(packet, packet);
                    } catch (NoSuchMethodException e) {
                        throwables.add(e);
                    }
                }
                if (throwables.size() == 0)
                    return;
            } catch (Exception ex) {
                throwables.add(ex);
            }
            System.err.println("While the packet " + packet.getClass() + " should be handled by this basic implementation, the following errors occurred.");
            System.err.println("In total, these are " + throwables.size() + " errors.");
            for (Throwable throwable : throwables) {
                throwable.printStackTrace();
            }*/
        }


        @Override
        public void handleLogin(LoginPacket packet) {
            handleMethodUse("handleLogin", packet);
        }

        @Override
        public void handleUpload(TitleDataUploadPacket packet) {
            handleMethodUse("handleUpload", packet);
        }

        @Override
        public void handleResponse(ServerResponsePacket packet) {
            handleMethodUse("handleResponse", packet);
        }

        @Override
        public void handleTitleData(TitleDataPacket packet) {
            handleMethodUse("handleTitleData", packet);
        }

        @Override
        public void handleTitle(TitlePacket packet) {
            handleMethodUse("handleTitle", packet);
        }

        @Override
        public void handleSearchRequest(SearchRequestPacket packet) {
            handleMethodUse("handleSearchRequest", packet);
        }

        @Override
        public void handleSearchSuggestions(SearchSuggestionPacket packet) {
            handleMethodUse("handleSearchSuggestions", packet);
        }

        @Override
        public void handleStartPlay(StartPlayPacket packet) {
            handleMethodUse("handleStartPlay", packet);
        }

        @Override
        public void handleTitlePlay(TitlePlayPacket packet) {
            handleMethodUse("handleTitlePlay", packet);
        }
    }
}
